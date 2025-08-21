package br.upf.ads175.repository;

import br.upf.ads175.dto.ProdutoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProdutoRepository - Testes Unitários")
class ProdutoRepositoryTest {

    private ProdutoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProdutoRepository();
    }

    @Test
    @DisplayName("Deve encontrar produto por ID existente")
    void deveEncontrarProdutoPorIdExistente() {
        // Given - Dado um ID que sabemos que existe
        Long idExistente = 1L;

        // When - Quando buscamos por esse ID
        Optional<ProdutoDTO> resultado = repository.findById(idExistente);

        // Then - Então deve retornar o produto correto
        assertTrue(resultado.isPresent(), "Produto deveria ser encontrado");
        assertEquals(idExistente, resultado.get().id(), "ID deve ser o esperado");
        assertEquals("Notebook Gamer", resultado.get().nome(), "Nome deve ser o esperado");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void deveRetornarVazioParaIdInexistente() {
        // Given - Dado um ID que sabemos que não existe
        Long idInexistente = 999L;

        // When - Quando buscamos por esse ID
        Optional<ProdutoDTO> resultado = repository.findById(idInexistente);

        // Then - Então deve retornar Optional vazio
        assertTrue(resultado.isEmpty(), "Produto não deveria ser encontrado");
    }
    @Test
    @DisplayName("Deve retornar apenas produtos ativos")
    void deveRetornarApenasProdutosAtivos() {
        // When - Quando buscamos produtos ativos
        List<ProdutoDTO> produtosAtivos = repository.findByAtivo(true);

        // Then - Então todos devem estar ativos
        assertFalse(produtosAtivos.isEmpty(), "Lista não deve estar vazia");
        assertTrue(produtosAtivos.stream().allMatch(ProdutoDTO::ativo),
                "Todos os produtos devem estar ativos");
    }

    @Test
    @DisplayName("Deve retornar apenas produtos inativos")
    void deveRetornarApenasProdutosInativos() {
        // When - Quando buscamos produtos inativos
        List<ProdutoDTO> produtosInativos = repository.findByAtivo(false);

        // Then - Então todos devem estar inativos
        assertFalse(produtosInativos.isEmpty(), "Lista não deve estar vazia");
        assertTrue(produtosInativos.stream().noneMatch(ProdutoDTO::ativo),
                "Nenhum produto deve estar ativo");
    }

    @Test
    @DisplayName("Deve retornar produtos da categoria Eletrônicos")
    void deveRetornarProdutosDaCategoriaEletronicos() {
        // Given - Dada uma categoria específica
        String categoria = "Eletrônicos";

        // When - Quando buscamos por essa categoria
        List<ProdutoDTO> produtos = repository.findByCategoria(categoria);

        // Then - Então todos devem ser da categoria correta
        assertFalse(produtos.isEmpty(), "Lista não deve estar vazia");
        assertTrue(produtos.stream()
                        .allMatch(p -> p.categoria().nome().equalsIgnoreCase(categoria)),
                "Todos os produtos devem ser da categoria " + categoria);
    }
    @Test
    @DisplayName("Deve retornar produtos dentro da faixa de preço")
    void deveRetornarProdutosDentroFaixaPreco() {
        // Given - Dada uma faixa de preço
        BigDecimal precoMin = new BigDecimal("100.00");
        BigDecimal precoMax = new BigDecimal("1000.00");

        // When - Quando buscamos nessa faixa
        List<ProdutoDTO> produtos = repository.findByPrecoEntre(precoMin, precoMax);

        // Then - Então todos devem estar na faixa
        assertFalse(produtos.isEmpty(), "Lista não deve estar vazia");
        assertTrue(produtos.stream()
                        .allMatch(p -> p.preco().compareTo(precoMin) >= 0
                                && p.preco().compareTo(precoMax) <= 0),
                "Todos os produtos devem estar na faixa de preço");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para faixa de preço sem produtos")
    void deveRetornarListaVaziaParaFaixaSemProdutos() {
        // Given - Dada uma faixa onde não há produtos
        BigDecimal precoMin = new BigDecimal("10000.00");
        BigDecimal precoMax = new BigDecimal("20000.00");

        // When - Quando buscamos nessa faixa
        List<ProdutoDTO> produtos = repository.findByPrecoEntre(precoMin, precoMax);

        // Then - Então a lista deve estar vazia
        assertTrue(produtos.isEmpty(), "Lista deve estar vazia para faixa sem produtos");
    }
}