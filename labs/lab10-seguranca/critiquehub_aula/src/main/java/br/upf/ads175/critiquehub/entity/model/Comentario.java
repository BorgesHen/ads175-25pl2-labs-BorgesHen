package br.upf.ads175.critiquehub.entity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade para comentários em avaliações
 * Suporta apenas um nível: comentário principal e respostas diretas
 */
@Entity
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliacao_id", nullable = false)
    private Avaliacao avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    // Autorelacionamento: comentário pode ter um "pai"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comentario_pai_id")
    private Comentario comentarioPai;

    // Lista de respostas diretas a este comentário
    @OneToMany(mappedBy = "comentarioPai", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataComentario ASC")
    private List<Comentario> respostas = new ArrayList<>();

    @NotBlank(message = "Conteúdo do comentário é obrigatório")
    @Size(max = 1000, message = "Comentário não pode exceder 1000 caracteres")
    @Column(name = "conteudo", nullable = false, length = 1000)
    private String conteudo;

    @Column(name = "data_comentario", nullable = false)
    private LocalDateTime dataComentario;

    // Construtor padrão
    protected Comentario() {}

    // Construtor para comentário principal
    public Comentario(Avaliacao avaliacao, Usuario autor, String conteudo) {
        this.avaliacao = avaliacao;
        this.autor = autor;
        this.conteudo = conteudo;
        this.dataComentario = LocalDateTime.now();
        this.comentarioPai = null; // É um comentário principal
    }

    // Construtor para resposta
    public Comentario(Avaliacao avaliacao, Usuario autor, String conteudo, Comentario comentarioPai) {
        this.avaliacao = avaliacao;
        this.autor = autor;
        this.conteudo = conteudo;
        this.comentarioPai = comentarioPai;
        this.dataComentario = LocalDateTime.now();

        // Adicionar automaticamente à lista de respostas do pai
        if (comentarioPai != null) {
            comentarioPai.getRespostas().add(this);
        }
    }

    // Métodos de conveniência
    public boolean isComentarioPrincipal() {
        return comentarioPai == null;
    }

    public boolean isResposta() {
        return comentarioPai != null;
    }

    public void adicionarResposta(Usuario autor, String conteudo) {
        if (!isComentarioPrincipal()) {
            throw new IllegalStateException("Apenas comentários principais podem receber respostas");
        }

        Comentario resposta = new Comentario(this.avaliacao, autor, conteudo, this);
        // A resposta é automaticamente adicionada à lista pelo construtor
    }

    public int getNumeroRespostas() {
        return respostas.size();
    }

    @PrePersist
    private void validarAntesPersistir() {
        if (dataComentario == null) {
            dataComentario = LocalDateTime.now();
        }

        // Validar que respostas não podem ter respostas (apenas 1 nível)
        if (comentarioPai != null && comentarioPai.getComentarioPai() != null) {
            throw new IllegalStateException("Não é permitido mais de um nível de aninhamento");
        }
    }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Avaliacao getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public Comentario getComentarioPai() { return comentarioPai; }
    public void setComentarioPai(Comentario comentarioPai) { this.comentarioPai = comentarioPai; }

    public List<Comentario> getRespostas() { return respostas; }
    public void setRespostas(List<Comentario> respostas) { this.respostas = respostas; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataComentario() { return dataComentario; }
    public void setDataComentario(LocalDateTime dataComentario) { this.dataComentario = dataComentario; }
}
