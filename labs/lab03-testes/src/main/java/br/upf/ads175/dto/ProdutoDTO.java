package br.upf.ads175.dto;

import java.math.BigDecimal;

public record ProdutoDTO(
        Long id,
        String nome,
        BigDecimal preco,
        boolean ativo,
        CategoriaDTO categoria
) {
    // Métodos utilitários no record
    public boolean isPremium() {
        return preco.compareTo(BigDecimal.valueOf(1000)) > 0;
    }

    public String getNomeExibicao() {
        return ativo ? nome : nome + " (Inativo)";
    }

    public static int fromEntity(Object o) {
        return 0;
    }
}