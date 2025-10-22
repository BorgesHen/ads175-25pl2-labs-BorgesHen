package br.upf.ads175.critiquehub.entity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Entidade que representa uma tag no CritiqueHub.
 */
@Entity
@Table(name = "tags")
@NamedQueries({
    @NamedQuery(name = "Tag.buscarPorNome",
        query = "SELECT t FROM Tag t WHERE LOWER(t.nome) LIKE LOWER(:nome) AND t.ativo = true ORDER BY t.nome"),
    @NamedQuery(name = "Tag.listarAtivas",
        query = "SELECT t FROM Tag t WHERE t.ativo = true ORDER BY t.nome"),
    @NamedQuery(name = "Tag.buscarMaisUsadas",
        query = "SELECT t FROM Tag t WHERE t.ativo = true AND SIZE(t.itens) > 0 ORDER BY SIZE(t.itens) DESC")
})
public class Tag extends BaseEntity {

    @NotBlank(message = "Nome da tag é obrigatório")
    @Size(min = 2, max = 50, message = "Nome da tag deve ter entre 2 e 50 caracteres")
    @Column(name = "nome", unique = true, nullable = false, length = 50)
    private String nome;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato hexadecimal (#RRGGBB)")
    @Column(name = "cor", length = 7)
    private String cor;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Column(name = "descricao", length = 200)
    private String descricao;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    /**
     * Lado inverso do relacionamento Many-to-Many com ItemCultural.
     */
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<ItemCultural> itens = new HashSet<>();

    protected Tag() {
        this.ativo = true;
    }

    public Tag(String nome) {
        this();
        this.nome = normalizarNome(nome);
        this.cor = gerarCorAleatoria();
    }

    public Tag(String nome, String descricao) {
        this(nome);
        this.descricao = descricao != null ? descricao.trim() : null;
    }

    private String normalizarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da tag não pode ser vazio");
        }
        return nome.toLowerCase().trim();
    }

    private String gerarCorAleatoria() {
        Random random = new Random();
        String[] coresPredefinidas = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#FFB347", "#87CEEB", "#F0E68C", "#FF69B4",
            "#20B2AA", "#778899", "#B0C4DE", "#F5DEB3", "#FF7F50"
        };
        return coresPredefinidas[random.nextInt(coresPredefinidas.length)];
    }

    /**
     * Adiciona um item cultural à tag (método auxiliar para sincronização).
     */
    public void adicionarItem(ItemCultural item) {
        if (item != null) {
            this.itens.add(item);
        }
    }

    /**
     * Remove um item cultural da tag (método auxiliar para sincronização).
     */
    public void removerItem(ItemCultural item) {
        if (item != null) {
            this.itens.remove(item);
        }
    }

    // Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = normalizarNome(nome);
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao != null ? descricao.trim() : null;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<ItemCultural> getItens() {
        return new HashSet<>(itens);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return nome != null && nome.equals(tag.nome);
    }

    @Override
    public int hashCode() {
        return nome != null ? nome.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Tag{id=%d, nome='%s', cor='%s', ativo=%s}",
            getId(), nome, cor, ativo);
    }
}
