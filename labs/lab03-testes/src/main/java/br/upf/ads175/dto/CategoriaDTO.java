package br.upf.ads175.dto;

public record CategoriaDTO(String nome) {
    // Validação customizada no record
    public CategoriaDTO {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser nulo ou vazio");
        }
    }
}