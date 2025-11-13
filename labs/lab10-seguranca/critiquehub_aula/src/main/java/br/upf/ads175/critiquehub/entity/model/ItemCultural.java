package br.upf.ads175.critiquehub.entity.model;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;
import br.upf.ads175.critiquehub.entity.enums.TipoItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entidade que representa um item cultural no CritiqueHub.
 */
@Entity
@Table(name = "itens_culturais")
@NamedQueries({
    @NamedQuery(name = "ItemCultural.buscarPorTitulo",
        query = "SELECT i FROM ItemCultural i WHERE LOWER(i.titulo) LIKE LOWER(:titulo) AND i.ativo = true ORDER BY i.titulo"),
    @NamedQuery(name = "ItemCultural.listarPorTipo",
        query = "SELECT i FROM ItemCultural i WHERE i.tipo = :tipo AND i.ativo = true ORDER BY i.titulo"),
    @NamedQuery(name = "ItemCultural.buscarMaisAvaliados",
        query = "SELECT i FROM ItemCultural i WHERE i.ativo = true AND SIZE(i.tags) > 0 ORDER BY SIZE(i.tags) DESC"),
    @NamedQuery(name = "ItemCultural.buscarMelhoresAvaliados",
        query = "SELECT i FROM ItemCultural i WHERE i.ativo = true ORDER BY i.titulo"),
    @NamedQuery(name = "ItemCultural.buscarLancamentosRecentes",
        query = "SELECT i FROM ItemCultural i WHERE i.ativo = true AND i.dataLancamento >= :dataInicio ORDER BY i.dataLancamento DESC")
})
public class ItemCultural extends BaseEntity {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Size(max = 2000, message = "Sinopse deve ter no máximo 2000 caracteres")
    @Column(name = "sinopse", length = 2000)
    private String sinopse;

    @Column(name = "data_lancamento")
    private LocalDate dataLancamento;

    @NotNull(message = "Tipo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoItem tipo;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    /**
     * Lado proprietário do relacionamento Many-to-Many com Tag.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "item_cultural_tag",
        joinColumns = @JoinColumn(name = "item_cultural_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    // Adicionar na classe ItemCultural
    @OneToMany(mappedBy = "itemCultural", cascade = CascadeType.ALL)
    private List<ListaFavoritos> favoritadoPor = new ArrayList<>();

    // Método para verificar se é favorito de um usuário
    public boolean isFavoritoDoUsuario(Usuario usuario) {
        return favoritadoPor.stream()
            .anyMatch(f -> f.getUsuario().getId().equals(usuario.getId()));
    }

    // Método para obter categoria do favorito para um usuário específico
    public Optional<CategoriaFavorito> getCategoriaFavorito(Usuario usuario) {
        return favoritadoPor.stream()
            .filter(f -> f.getUsuario().getId().equals(usuario.getId()))
            .map(ListaFavoritos::getCategoria)
            .findFirst();
    }


    protected ItemCultural() {
        this.ativo = true;
    }

    public ItemCultural(String titulo, TipoItem tipo, LocalDate dataLancamento) {
        this();
        this.titulo = titulo != null ? titulo.trim() : null;
        this.tipo = tipo;
        this.dataLancamento = dataLancamento;
    }

    /**
     * Adiciona uma tag ao item (gerencia relacionamento bidirecional).
     */
    public void adicionarTag(Tag tag) {
        if (tag != null && this.tags.add(tag)) {
            tag.adicionarItem(this);
        }
    }

    /**
     * Adiciona uma tag pelo nome (cria nova tag se necessário).
     */
    public void adicionarTag(String nomeTag) {
        if (nomeTag != null && !nomeTag.trim().isEmpty()) {
            Tag tag = new Tag(nomeTag);
            adicionarTag(tag);
        }
    }

    /**
     * Remove uma tag do item (gerencia relacionamento bidirecional).
     */
    public void removerTag(Tag tag) {
        if (tag != null && this.tags.remove(tag)) {
            tag.removerItem(this);
        }
    }

    /**
     * Remove todas as tags do item.
     */
    public void limparTags() {
        // Cria uma cópia para evitar ConcurrentModificationException
        Set<Tag> tagsParaRemover = new HashSet<>(this.tags);
        tagsParaRemover.forEach(this::removerTag);
    }

    /**
     * Verifica se o item possui uma tag específica.
     */
    public boolean possuiTag(Tag tag) {
        return tag != null && this.tags.contains(tag);
    }

    /**
     * Verifica se o item possui uma tag com o nome especificado.
     */
    public boolean possuiTag(String nomeTag) {
        if (nomeTag == null || nomeTag.trim().isEmpty()) {
            return false;
        }
        String nomeNormalizado = nomeTag.toLowerCase().trim();
        return this.tags.stream()
            .anyMatch(tag -> nomeNormalizado.equals(tag.getNome()));
    }

    // Getters and Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo.trim() : null;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse != null ? sinopse.trim() : null;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<Tag> getTags() {
        return new HashSet<>(tags);
    }

    @Override
    public String toString() {
        return String.format("ItemCultural{id=%d, titulo='%s', tipo=%s, totalTags=%d}",
            getId(), titulo, tipo, tags.size());
    }
}
