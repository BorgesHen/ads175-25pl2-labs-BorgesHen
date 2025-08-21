package br.upf.ads175.service;

import br.upf.ads175.dto.CategoriaDTO;
import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoService - Testes Unitários com Mocks")
class ProdutoServiceTest {

    @Mock
    ProdutoRepository repository;

    @InjectMocks
    ProdutoService service;

    private List<ProdutoDTO> produtosMock;

    @BeforeEach
    void setUp() {
        // Dados de teste que usaremos nos mocks
        produtosMock = List.of(
                new ProdutoDTO(1L, "Produto A", new BigDecimal("500.00"), true, new CategoriaDTO("Cat1")),
                new ProdutoDTO(2L, "Produto B", new BigDecimal("1500.00"), true, new CategoriaDTO("Cat2")),
                new ProdutoDTO(3L, "Produto C", new BigDecimal("300.00"), false, new CategoriaDTO("Cat1"))
        );
    }
    @Test
    @DisplayName("Deve retornar produtos ativos ordenados por nome")
    void deveRetornarProdutosAtivosOrdenadosPorNome() {
        // Given - Mock configurado para retornar produtos ativos
        when(repository.findByAtivo(true)).thenReturn(
                produtosMock.stream()
                        .filter(ProdutoDTO::ativo)
                        .toList()
        );

        // When - Executamos o método do serviço
        List<ProdutoDTO> resultado = service.buscarProdutosAtivosOrdenadosPorNome();

        // Then - Verificamos o resultado e as interações
        assertFalse(resultado.isEmpty(), "Lista não deve estar vazia");
        assertTrue(resultado.stream().allMatch(ProdutoDTO::ativo),
                "Todos devem estar ativos");

        // Verifica se está ordenado por nome
        assertEquals("Produto A", resultado.get(0).nome());
        assertEquals("Produto B", resultado.get(1).nome());

        // Verifica se o repositório foi chamado corretamente
        verify(repository, times(1)).findByAtivo(true);
    }
    @Test
    @DisplayName("Deve retornar produto quando ID existe")
    void deveRetornarProdutoQuandoIdExiste() {
        // Given - Mock configurado para retornar um produto específico
        Long idExistente = 1L;
        ProdutoDTO produtoEsperado = produtosMock.get(0);
        when(repository.findById(idExistente)).thenReturn(Optional.of(produtoEsperado));

        // When - Buscamos pelo ID
        Optional<ProdutoDTO> resultado = service.buscarPorId(idExistente);

        // Then - Deve retornar o produto correto
        assertTrue(resultado.isPresent(), "Produto deve ser encontrado");
        assertEquals(produtoEsperado.id(), resultado.get().id(), "ID deve ser igual");

        // Verifica interação com o repositório
        verify(repository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não existe")
    void deveRetornarVazioQuandoIdNaoExiste() {
        // Given - Mock configurado para não encontrar o produto
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        // When - Buscamos pelo ID inexistente
        Optional<ProdutoDTO> resultado = service.buscarPorId(idInexistente);

        // Then - Deve retornar Optional vazio
        assertTrue(resultado.isEmpty(), "Resultado deve estar vazio");

        // Verifica que o repositório foi consultado
        verify(repository, times(1)).findById(idInexistente);
    }
    @Test
    @DisplayName("Deve retornar apenas produtos premium ativos ordenados por preço decrescente")
    void deveRetornarProdutosPremiumOrdenadosPorPreco() {
        // Given - Mock retorna produtos ativos (incluindo premium e não-premium)
        when(repository.findByAtivo(true)).thenReturn(
                produtosMock.stream()
                        .filter(ProdutoDTO::ativo)
                        .toList()
        );

        // When - Buscamos produtos premium
        List<ProdutoDTO> resultado = service.buscarProdutosPremium();

        // Then - Deve retornar apenas produtos premium ordenados por preço
        assertFalse(resultado.isEmpty(), "Deve haver produtos premium");

        // Verifica se todos são premium (preço > 1000)
        assertTrue(resultado.stream().allMatch(ProdutoDTO::isPremium),
                "Todos devem ser premium");

        // Verifica ordenação por preço decrescente
        for (int i = 0; i < resultado.size() - 1; i++) {
            assertTrue(resultado.get(i).preco().compareTo(resultado.get(i + 1).preco()) >= 0,
                    "Deve estar ordenado por preço decrescente");
        }

        // Verifica interação com repositório
        verify(repository, times(1)).findByAtivo(true);
    }
    @Test
    @DisplayName("Deve lançar exceção para preços inválidos em busca por faixa")
    void deveLancarExcecaoParaPrecosInvalidos() {
        // Given - Preço mínimo maior que máximo (inválido)
        BigDecimal precoMin = new BigDecimal("1000.00");
        BigDecimal precoMax = new BigDecimal("500.00");

        // When & Then - Deve lançar exceção
        assertThrows(IllegalArgumentException.class, () -> {
            service.buscarProdutosPorFaixaPreco(precoMin, precoMax);
        }, "Deve lançar exceção para faixa de preço inválida");
    }

}