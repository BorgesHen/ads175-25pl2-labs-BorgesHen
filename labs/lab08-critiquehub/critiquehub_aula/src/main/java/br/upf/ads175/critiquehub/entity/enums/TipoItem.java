package br.upf.ads175.critiquehub.entity.enums;

/**
 * Enumera os tipos de itens culturais suportados pela plataforma.
 */
public enum TipoItem {

    FILME("Filme cinematográfico"),
    LIVRO("Livro ou publicação literária"),
    JOGO("Jogo eletrônico");

    private final String descricao;

    TipoItem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
