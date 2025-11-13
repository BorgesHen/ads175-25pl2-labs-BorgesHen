package br.upf.ads175.critiquehub.entity.enums;

/**
 * Categorias possíveis para itens favoritos
 */
public enum CategoriaFavorito {
    QUERO_ASSISTIR("Quero Assistir"),
    RECOMENDADO("Recomendado"),
    FAVORITO_ABSOLUTO("Favorito Absoluto"),
    PARA_REVISITAR("Para Revisitar"),
    ASSISTINDO("Assistindo Atualmente");

    private final String descricao;

    CategoriaFavorito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
