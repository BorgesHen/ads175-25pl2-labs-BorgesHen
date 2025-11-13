package br.upf.ads175.critiquehub.entity.enums;

/**
 * Representa o status de consumo de um item cultural por um usuário.
 */
public enum StatusConsumo {

    FINALIZADO("Completamente consumido"),
    EM_ANDAMENTO("Atualmente consumindo"),
    PLANEJADO("Planejado para consumo futuro");

    private final String descricao;

    StatusConsumo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean permiteAvaliacao() {
        return this == FINALIZADO || this == EM_ANDAMENTO;
    }
}
