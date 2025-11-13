package br.upf.ads175.critiquehub.entity.model;

import br.upf.ads175.critiquehub.entity.enums.CategoriaFavorito;import br.upf.ads175.critiquehub.entity.enums.StatusUsuario;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;import java.util.ArrayList;import java.util.List;import java.util.stream.Collectors;

/**
 * Entidade principal representando um usuário da plataforma CritiqueHub.
 *
 * Demonstra conceitos fundamentais de JPA:
 * - Mapeamento básico de entidade
 * - Uso de enumerações
 * - Named Queries essenciais
 * - Métodos de negócio simples
 */
@Entity // <1>
@Table(name = "usuarios") // <2>
@NamedQueries({ // <3>
    @NamedQuery(
        name = "Usuario.buscarPorEmail",
        query = "SELECT u FROM Usuario u WHERE u.email = :email"
    ),
    @NamedQuery(
        name = "Usuario.buscarPorNomeUsuario",
        query = "SELECT u FROM Usuario u WHERE u.nomeUsuario = :nomeUsuario"
    ),
    @NamedQuery(
        name = "Usuario.listarAtivos",
        query = "SELECT u FROM Usuario u WHERE u.status = 'ATIVO' ORDER BY u.dataRegistro DESC"
    )
})
public class Usuario extends BaseEntity {

    // ========================================================================
    // Atributos Básicos
    // ========================================================================

    /**
     * Email único do usuário.
     */
    @Column(name = "email", nullable = false, unique = true, length = 255) // <4>
    private String email;

    /**
     * Nome de usuário público - identificador único na plataforma.
     */
    @Column(name = "nome_usuario", nullable = false, unique = true, length = 30) // <5>
    private String nomeUsuario;


    @NotBlank(message = "A senha é obrigatória")
    @Size(min=6, message = "A senha deve ter no mínimo 6 caracteres!")
    @Column(name = "senha", nullable = false, length = 255)
    @Password
    private String senha;

    /**
     * Nome completo do usuário.
     */
    @Column(name = "nome_completo", nullable = false, length = 150)
    private String nomeCompleto;

    /**
     * Biografia opcional do usuário.
     */
    @Column(name = "biografia", length = 500)
    private String biografia;

    /**
     * Data de nascimento para cálculo de idade.
     */
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    /**
     * Data de registro na plataforma.
     */
    @Column(name = "data_registro", nullable = false, updatable = false) // <6>
    private LocalDate dataRegistro;

    /**
     * Status atual do usuário.
     */
    @Enumerated(EnumType.STRING) // <7>
    @Column(name = "status", nullable = false, length = 20)
    private StatusUsuario status;

    /**
     * Indica se o perfil é público.
     */
    @Column(name = "perfil_publico", nullable = false)
    private Boolean perfilPublico = true;

    // Adicionar na classe Usuario
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaFavoritos> favoritos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            indexes = @Index (name = "idx_usuarios_roles",columnList = 'usuario_id')
    )
    @Column(name="role", length = 50)
    @Roles
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    private boolean ativo = true ;

    @Column(Nullable = false, updatable = false)
    private LocalDateTime;

    @Column(Nullable = false)
    private LocalDateTime;

    @Column
    private LocalDateTime;


    // Método de conveniência
    public void adicionarFavorito(ItemCultural item, CategoriaFavorito categoria, String observacoes) {
        ListaFavoritos favorito = new ListaFavoritos(this, item, categoria, observacoes);
        favoritos.add(favorito);
    }

    public void removerFavorito(ItemCultural item) {
        favoritos.removeIf(f -> f.getItemCultural().getId().equals(item.getId()));
    }

    public List<ListaFavoritos> getFavoritosPorCategoria(CategoriaFavorito categoria) {
        return favoritos.stream()
            .filter(f -> f.getCategoria() == categoria)
            .collect(Collectors.toList());
    }

    // ========================================================================
    // Construtores
    // ========================================================================

    /**
     * Construtor padrão JPA.
     */
    protected Usuario() { // <8>
        this.status = StatusUsuario.ATIVO;
        this.dataRegistro = LocalDate.now();
    }

    /**
     * Construtor para criação de novos usuários.
     */
    public Usuario(String email, String nomeUsuario, String nomeCompleto) { // <9>
        this();
        this.email = email;
        this.nomeUsuario = nomeUsuario;
        this.nomeCompleto = nomeCompleto;
    }

    // ========================================================================
    // Lifecycle Callbacks
    // ========================================================================

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = criadoEm;
    }

    @PreUpdate
    protected void onUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // ========================================================================
    // Métodos de Negócio
    // ========================================================================

    /**
     * Verifica se o usuário está ativo.
     */
    public boolean isAtivo() { // <10>
        return StatusUsuario.ATIVO.equals(this.status);
    }

    /**
     * Verifica se o perfil é público.
     */
    public boolean isPerfilPublico() {
        return Boolean.TRUE.equals(perfilPublico);
    }

    /**
     * Calcula a idade do usuário.
     */
    public Integer getIdade() { // <11>
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    // ========================================================================
    // Getters e Setters
    // ========================================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha(){
        return senha;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }

    public Boolean getPerfilPublico() {
        return perfilPublico;
    }

    public void setPerfilPublico(Boolean perfilPublico) {
        this.perfilPublico = perfilPublico;
    }

    @Override
    public String toString() { // <12>
        return String.format("Usuario{id=%d, nomeUsuario='%s', status=%s}",
                           id, nomeUsuario, status);
    }
}
