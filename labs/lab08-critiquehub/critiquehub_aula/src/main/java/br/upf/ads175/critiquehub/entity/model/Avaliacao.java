package br.upf.ads175.critiquehub.entity.model;

import br.upf.ads175.critiquehub.entity.enums.StatusConsumo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Entidade que representa a avaliação de um usuário sobre um item cultural.
 *
 * Esta entidade demonstra conceitos avançados de JPA:
 * - Relacionamentos bidirecionais complexos
 * - Validações de negócio integradas
 * - Gestão de dependências com cascade
 * - Consultas nomeadas otimizadas
 * - Métodos de conveniência para manipulação
 *
 * Características principais:
 * - Cada usuário pode avaliar um item apenas uma vez
 * - Notas seguem escala de 1 a 10
 * - Status de consumo influencia algoritmos de recomendação
 * - Suporte a comentários aninhados
 * - Auditoria automática de criação e modificação
 */
@Entity
@Table(name = "avaliacoes",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_usuario_item",
           columnNames = {"usuario_id", "item_cultural_id"}
       ))
@NamedQueries({
    @NamedQuery(
        name = "Avaliacao.buscarPorUsuario",
        query = """
            SELECT a FROM Avaliacao a
            JOIN FETCH a.itemCultural ic
            WHERE a.usuario.id = :usuarioId
            ORDER BY a.dataAvaliacao DESC
            """
    ),
    @NamedQuery(
        name = "Avaliacao.buscarPorItem",
        query = """
            SELECT a FROM Avaliacao a
            JOIN FETCH a.usuario u
            WHERE a.itemCultural.id = :itemId
            AND a.publica = true
            ORDER BY a.dataAvaliacao DESC
            """
    ),
    @NamedQuery(
        name = "Avaliacao.calcularMediaPorItem",
        query = """
            SELECT AVG(a.nota) FROM Avaliacao a
            WHERE a.itemCultural.id = :itemId
            AND a.nota IS NOT NULL
            """
    ),
    @NamedQuery(
        name = "Avaliacao.buscarRecomendacoes",
        query = """
            SELECT a FROM Avaliacao a
            WHERE a.usuario.id IN :usuariosComGostosSimilares
            AND a.nota >= 8
            AND a.itemCultural.id NOT IN :itensJaAvaliados
            ORDER BY a.nota DESC, a.dataAvaliacao DESC
            """
    )
})
public class Avaliacao extends BaseEntity {

    // ========================================================================
    // Relacionamentos Principais
    // ========================================================================

    /**
     * Usuário que realizou a avaliação.
     * Relacionamento muitos-para-um com fetch LAZY para otimização.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "Usuário é obrigatório para uma avaliação")
    private Usuario usuario;

    /**
     * Item cultural sendo avaliado.
     * Relacionamento muitos-para-um com fetch LAZY para otimização.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_cultural_id", nullable = false)
    @NotNull(message = "Item cultural é obrigatório para uma avaliação")
    private ItemCultural itemCultural;

    // ========================================================================
    // Dados da Avaliação
    // ========================================================================

    /**
     * Nota atribuída ao item (escala de 1 a 10).
     * Pode ser nula quando usuário ainda não atribuiu nota.
     */
    @Column(name = "nota")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 10, message = "Nota máxima é 10")
    private Integer nota;

    /**
     * Status de consumo do item pelo usuário.
     * Influencia algoritmos de recomendação e filtros.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status_consumo", nullable = false)
    @NotNull(message = "Status de consumo é obrigatório")
    private StatusConsumo statusConsumo;

    /**
     * Texto da resenha escrita pelo usuário.
     * Campo opcional para avaliações mais detalhadas.
     */
    @Column(name = "resenha", length = 2000)
    @Size(max = 2000, message = "Resenha não pode exceder 2000 caracteres")
    private String resenha;

    /**
     * Indica se o usuário recomenda o item.
     * Campo boolean para simplificar filtros de recomendação.
     */
    @Column(name = "recomenda")
    private Boolean recomenda;

    /**
     * Indica se a avaliação é pública.
     * Controla visibilidade para outros usuários.
     */
    @Column(name = "publica", nullable = false)
    @NotNull
    private Boolean publica = true;

    /**
     * Data e hora da avaliação.
     * Importante para ordenação temporal e analytics.
     */
    @Column(name = "data_avaliacao", nullable = false)
    @NotNull
    private LocalDateTime dataAvaliacao;

    /**
     * Data da última modificação da avaliação.
     * Permite rastrear atualizações de resenhas.
     */
    @Column(name = "data_modificacao")
    private LocalDateTime dataModificacao;

    // ========================================================================
    // Relacionamentos Dependentes
    // ========================================================================

    /**
     * Comentários feitos nesta avaliação por outros usuários.
     * Relacionamento um-para-muitos com cascade completo.
     */
    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios = new ArrayList<>();

    // ========================================================================
    // Construtores
    // ========================================================================

    /**
     * Construtor padrão para JPA.
     */
    protected Avaliacao() {
        this.dataAvaliacao = LocalDateTime.now();
        this.publica = true;
    }

    /**
     * Construtor para criação de nova avaliação.
     *
     * @param usuario usuário que está avaliando
     * @param itemCultural item sendo avaliado
     * @param statusConsumo status atual de consumo
     */
    public Avaliacao(Usuario usuario, ItemCultural itemCultural, StatusConsumo statusConsumo) {
        this();
        this.usuario = usuario;
        this.itemCultural = itemCultural;
        this.statusConsumo = statusConsumo;
    }

    /**
     * Construtor completo para avaliação com nota.
     *
     * @param usuario usuário que está avaliando
     * @param itemCultural item sendo avaliado
     * @param nota nota atribuída (1-10)
     * @param statusConsumo status atual de consumo
     */
    public Avaliacao(Usuario usuario, ItemCultural itemCultural, Integer nota, StatusConsumo statusConsumo) {
        this(usuario, itemCultural, statusConsumo);
        this.nota = nota;
        this.recomenda = (nota != null && nota >= 7); // Auto-recomenda se nota >= 7
    }

    // ========================================================================
    // Métodos de Negócio
    // ========================================================================

    /**
     * Verifica se a avaliação possui nota atribuída.
     */
    public boolean temNota() {
        return this.nota != null;
    }

    /**
     * Verifica se a avaliação possui resenha escrita.
     */
    public boolean temResenha() {
        return this.resenha != null && !this.resenha.trim().isEmpty();
    }

    /**
     * Verifica se o item foi completamente consumido.
     */
    public boolean foiCompleto() {
        return StatusConsumo.FINALIZADO.equals(this.statusConsumo);
    }

    /**
     * Verifica se a avaliação permite ser comentada por outros usuários.
     */
    public boolean permiteComentarios() {
        return this.publica && (temNota() || temResenha());
    }

    /**
     * Atualiza a nota e ajusta automaticamente a recomendação.
     *
     * @param novaNota nova nota a ser atribuída
     */
    public void atualizarNota(Integer novaNota) {
        if (novaNota != null && (novaNota < 1 || novaNota > 10)) {
            throw new IllegalArgumentException("Nota deve estar entre 1 e 10");
        }

        this.nota = novaNota;
        this.dataModificacao = LocalDateTime.now();

        // Atualiza recomendação automaticamente baseada na nota
        if (novaNota != null) {
            this.recomenda = novaNota >= 7;
        }
    }

    /**
     * Atualiza a resenha e marca data de modificação.
     *
     * @param novaResenha novo texto da resenha
     */
    public void atualizarResenha(String novaResenha) {
        this.resenha = (novaResenha != null && !novaResenha.trim().isEmpty())
                      ? novaResenha.trim()
                      : null;
        this.dataModificacao = LocalDateTime.now();
    }

    /**
     * Atualiza o status de consumo.
     *
     * @param novoStatus novo status de consumo
     */
    public void atualizarStatus(StatusConsumo novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("Status de consumo não pode ser nulo");
        }

        this.statusConsumo = novoStatus;
        this.dataModificacao = LocalDateTime.now();
    }

    // ========================================================================
    // Métodos de Gestão de Comentários
    // ========================================================================

    /**
     * Adiciona um comentário à avaliação.
     *
     * @param autor usuário que está comentando
     * @param conteudo texto do comentário
     * @return o comentário criado
     * @throws IllegalStateException se avaliação não permite comentários
     */
    public Comentario adicionarComentario(Usuario autor, String conteudo) {
        if (!permiteComentarios()) {
            throw new IllegalStateException("Esta avaliação não permite comentários");
        }

        if (autor.equals(this.usuario)) {
            throw new IllegalArgumentException("Usuário não pode comentar sua própria avaliação");
        }

        Comentario comentario = new Comentario(this, autor, conteudo);
        comentarios.add(comentario);
        return comentario;
    }

    /**
     * Remove um comentário da avaliação.
     *
     * @param comentario comentário a ser removido
     * @return true se removido com sucesso
     */
    public boolean removerComentario(Comentario comentario) {
        return comentarios.remove(comentario);
    }

    /**
     * Obtém o número total de comentários.
     */
    public int getNumeroComentarios() {
        return comentarios.size();
    }

    /**
     * Obtém apenas os comentários principais (não respostas).
     */
    public List<Comentario> getComentariosPrincipais() {
        return comentarios.stream()
                .filter(Comentario::isComentarioPrincipal)
                .collect(Collectors.toList());
    }

    /**
     * Obtém os comentários mais recentes primeiro.
     */
    public List<Comentario> getComentariosOrdenados() {
        return comentarios.stream()
                .sorted((c1, c2) -> c2.getDataComentario().compareTo(c1.getDataComentario()))
                .collect(Collectors.toList());
    }

    // ========================================================================
    // Métodos Utilitários
    // ========================================================================

    /**
     * Calcula há quanto tempo a avaliação foi feita.
     */
    public String getTempoDecorrido() {
        // Implementação simplificada - em produção usar biblioteca como Humanize
        LocalDateTime agora = LocalDateTime.now();
        long dias = java.time.Duration.between(dataAvaliacao, agora).toDays();

        if (dias == 0) return "Hoje";
        if (dias == 1) return "Ontem";
        if (dias < 7) return dias + " dias atrás";
        if (dias < 30) return (dias / 7) + " semanas atrás";
        return (dias / 30) + " meses atrás";
    }

    /**
     * Verifica se a avaliação foi modificada após criação.
     */
    public boolean foiModificada() {
        return dataModificacao != null && dataModificacao.isAfter(dataAvaliacao);
    }

    /**
     * Gera um resumo textual da avaliação.
     */
    public String gerarResumo() {
        StringBuilder resumo = new StringBuilder();

        if (temNota()) {
            resumo.append("Nota: ").append(nota).append("/10");
        }

        if (recomenda != null) {
            if (resumo.length() > 0) resumo.append(" - ");
            resumo.append(recomenda ? "Recomenda" : "Não recomenda");
        }

        if (resumo.length() > 0) resumo.append(" - ");
        resumo.append("Status: ").append(statusConsumo.getDescricao());

        return resumo.toString();
    }

    // ========================================================================
    // Callback Methods JPA
    // ========================================================================

    /**
     * Executado antes da persistência inicial.
     */
    @PrePersist
    protected void prePersist() {
        if (dataAvaliacao == null) {
            dataAvaliacao = LocalDateTime.now();
        }

        // Validação de negócio: não pode avaliar o mesmo item duas vezes
        // (esta validação também é garantida pela constraint única no banco)
    }

    /**
     * Executado antes de qualquer atualização.
     */
    @PreUpdate
    protected void preUpdate() {
        dataModificacao = LocalDateTime.now();
    }

    // ========================================================================
    // Getters e Setters
    // ========================================================================

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ItemCultural getItemCultural() {
        return itemCultural;
    }

    public void setItemCultural(ItemCultural itemCultural) {
        this.itemCultural = itemCultural;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
        // Auto-atualiza recomendação quando nota é definida diretamente
        if (nota != null) {
            this.recomenda = nota >= 7;
        }
    }

    public StatusConsumo getStatusConsumo() {
        return statusConsumo;
    }

    public void setStatusConsumo(StatusConsumo statusConsumo) {
        this.statusConsumo = statusConsumo;
    }

    public String getResenha() {
        return resenha;
    }

    public void setResenha(String resenha) {
        this.resenha = resenha;
    }

    public Boolean getRecomenda() {
        return recomenda;
    }

    public void setRecomenda(Boolean recomenda) {
        this.recomenda = recomenda;
    }

    public Boolean getPublica() {
        return publica;
    }

    public void setPublica(Boolean publica) {
        this.publica = publica;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    // ========================================================================
    // Implementação de equals e hashCode
    // ========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Avaliacao avaliacao = (Avaliacao) o;

        // Se ambos têm ID, compara pelos IDs
        if (this.getId() != null && avaliacao.getId() != null) {
            return Objects.equals(getId(), avaliacao.getId());
        }

        // Se não têm IDs, compara pela combinação única usuário + item
        return Objects.equals(usuario, avaliacao.usuario) &&
               Objects.equals(itemCultural, avaliacao.itemCultural);
    }

    @Override
    public int hashCode() {
        if (getId() != null) {
            return Objects.hash(getId());
        }

        // Hash baseado na chave natural (usuário + item)
        return Objects.hash(
            usuario != null ? usuario.getId() : null,
            itemCultural != null ? itemCultural.getId() : null
        );
    }

    @Override
    public String toString() {
        return String.format("Avaliacao{id=%d, usuario='%s', item='%s', nota=%d, status=%s}",
                           getId(),
                           usuario != null ? usuario.getNomeUsuario() : "null",
                           itemCultural != null ? itemCultural.getTitulo() : "null",
                           nota,
                           statusConsumo);
    }
}
