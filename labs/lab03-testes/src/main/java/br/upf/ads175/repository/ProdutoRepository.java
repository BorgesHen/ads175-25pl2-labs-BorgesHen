package br.upf.ads175.repository;

import br.upf.ads175.dto.CategoriaDTO;
import br.upf.ads175.dto.ProdutoDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepository {

    /**
     * Simula nossa "base de dados" - em uma aplicação real,
     * isso seria substituído por JPA/Hibernate
     */
    private List<ProdutoDTO> obterTodosProdutos() {
        return List.of(
                new ProdutoDTO(1L, "Notebook Gamer", new BigDecimal("8500.00"), true, new CategoriaDTO("Eletrônicos")),
                new ProdutoDTO(2L, "Cadeira de Escritório", new BigDecimal("1200.50"), true, new CategoriaDTO("Móveis")),
                new ProdutoDTO(3L, "Monitor 4K", new BigDecimal("2300.00"), false, new CategoriaDTO("Eletrônicos")),
                new ProdutoDTO(4L, "Mesa de Escritório", new BigDecimal("850.00"), true, new CategoriaDTO("Móveis")),
                new ProdutoDTO(5L, "Teclado Mecânico", new BigDecimal("450.00"), true, new CategoriaDTO("Eletrônicos")),
                new ProdutoDTO(6L, "Smartphone Premium", new BigDecimal("3500.00"), true, new CategoriaDTO("Eletrônicos")),
                new ProdutoDTO(7L, "Luminária LED", new BigDecimal("150.00"), false, new CategoriaDTO("Iluminação")),
                new ProdutoDTO(8L, "Cabo USB-C", new BigDecimal("45.00"), true, new CategoriaDTO("Eletrônicos"))
        );
    }

    /**
     * Busca todos os produtos (equivalente a SELECT * FROM produtos)
     */
    public List<ProdutoDTO> findAll() {
        return obterTodosProdutos();
    }

    /**
     * Busca produto por ID (equivalente a SELECT * FROM produtos WHERE id = ?)
     */
    public Optional<ProdutoDTO> findById(Long id) {
        return obterTodosProdutos().stream()
                .filter(produto -> produto.id().equals(id))
                .findFirst();
    }

    /**
     * Busca produtos ativos (equivalente a SELECT * FROM produtos WHERE ativo = true)
     */
    public List<ProdutoDTO> findByAtivo(boolean ativo) {
        return obterTodosProdutos().stream()
                .filter(produto -> produto.ativo() == ativo)
                .toList();
    }

    /**
     * Busca produtos por categoria (equivalente a SELECT * FROM produtos p JOIN categorias c ON...)
     */
    public List<ProdutoDTO> findByCategoria(String nomeCategoria) {
        return obterTodosProdutos().stream()
                .filter(produto -> produto.categoria().nome().equalsIgnoreCase(nomeCategoria))
                .toList();
    }

    /**
     * Busca produtos com preço entre valores (equivalente a SELECT * WHERE preco BETWEEN ? AND ?)
     */
    public List<ProdutoDTO> findByPrecoEntre(BigDecimal precoMin, BigDecimal precoMax) {
        return obterTodosProdutos().stream()
                .filter(produto -> produto.preco().compareTo(precoMin) >= 0
                        && produto.preco().compareTo(precoMax) <= 0)
                .toList();
    }

    /**
     * Conta produtos por categoria
     */
    public long countByCategoria(String nomeCategoria) {
        return obterTodosProdutos().stream()
                .filter(produto -> produto.categoria().nome().equalsIgnoreCase(nomeCategoria))
                .count();
    }

    public Collection<Object> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax) {
        return List.of();
    }
}