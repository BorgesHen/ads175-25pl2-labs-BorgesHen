package br.upf.ads175.critiquehub.entity.enums;

/**
 * Enumera os possíveis status de um usuário no sistema CritiqueHub.
 *
 */
public enum StatusUsuario {

    /**
     * Usuário ativo e com acesso completo às funcionalidades.
     */
    ATIVO("Usuário ativo no sistema"),

    /**
     * Usuário temporariamente suspenso.
     */
    SUSPENSO("Usuário temporariamente suspenso"),

    /**
     * Usuário inativo por escolha própria.
     */
    INATIVO("Usuário inativo por escolha própria");

    private final String descricao;

    StatusUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isAtivo() {
        return this == ATIVO;
    }
}
