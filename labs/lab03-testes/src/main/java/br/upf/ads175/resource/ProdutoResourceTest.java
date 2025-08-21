package br.upf.ads175.resource;

import br.upf.ads175.dto.CategoriaDTO;
import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.service.ProdutoService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@DisplayName("ProdutoResource - Testes de Integração REST")
class ProdutoResourceTest {

    @InjectMock
    ProdutoService produtoService;

    private List<ProdutoDTO> produtosMock = List.of(
            new ProdutoDTO(1L, "Notebook Gamer", new BigDecimal("8500.00"), true, new CategoriaDTO("Eletrônicos")),
            new ProdutoDTO(2L, "Cadeira de Escritório", new BigDecimal("1200.50"), true, new CategoriaDTO("Móveis"))
    );
    @Test
    @DisplayName("GET /produtos deve retornar lista de produtos ativos")
    void deveRetornarListaProdutosAtivos() {
        // Given - Mock configurado para retornar produtos
        when(produtoService.buscarProdutosAtivosOrdenadosPorNome())
                .thenReturn(produtosMock);

        // When & Then - Fazemos requisição HTTP e verificamos resposta
        given()
                .when()
                .get("/produtos")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(2))
                .body("[0].id", equalTo(1))
                .body("[0].nome", equalTo("Notebook Gamer"))
                .body("[1].id", equalTo(2));

        // Verifica que o serviço foi chamado
        verify(produtoService, times(1)).buscarProdutosAtivosOrdenadosPorNome();
    }
    @Test
    @DisplayName("GET /produtos/{id} deve retornar produto quando existe")
    void deveRetornarProdutoQuandoIdExiste() {
        // Given - Mock configurado para retornar produto específico
        Long idExistente = 1L;
        ProdutoDTO produtoEsperado = produtosMock.get(0);
        when(produtoService.buscarPorId(idExistente))
                .thenReturn(Optional.of(produtoEsperado));

        // When & Then - Fazemos requisição e verificamos resposta
        given()
                .when()
                .get("/produtos/{id}", idExistente)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(1))
                .body("nome", equalTo("Notebook Gamer"))
                .body("preco", equalTo(8500.00f))
                .body("ativo", equalTo(true));

        verify(produtoService, times(1)).buscarPorId(idExistente);
    }

    @Test
    @DisplayName("GET /produtos/{id} deve retornar 404 quando produto não existe")
    void deveRetornar404QuandoProdutoNaoExiste() {
        // Given - Mock configurado para não encontrar o produto
        Long idInexistente = 999L;
        when(produtoService.buscarPorId(idInexistente))
                .thenReturn(Optional.empty());

        // When & Then - Fazemos requisição e verificamos erro 404
        given()
                .when()
                .get("/produtos/{id}", idInexistente)
                .then()
                .statusCode(404)
                .contentType("application/json")
                .body("erro", equalTo("Produto não encontrado"))
                .body("id", equalTo(999));

        verify(produtoService, times(1)).buscarPorId(idInexistente);
    }
    @Test
    @DisplayName("GET /produtos/premium deve retornar apenas produtos premium")
    void deveRetornarApenasProdutosPremium() {
        // Given - Mock configurado para retornar apenas produtos premium
        List<ProdutoDTO> produtosPremium = List.of(produtosMock.get(0)); // Apenas o Notebook (>1000)
        when(produtoService.buscarProdutosPremium())
                .thenReturn(produtosPremium);

        // When & Then - Fazemos requisição e verificamos resposta
        given()
                .when()
                .get("/produtos/premium")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(1))
                .body("[0].nome", equalTo("Notebook Gamer"))
                .body("[0].preco", greaterThan(1000.0f));

        verify(produtoService, times(1)).buscarProdutosPremium();
    }
    @Test
    @DisplayName("GET /produtos/por-categoria deve retornar produtos agrupados")
    void deveRetornarProdutosAgrupados() {
        // Given - Mock configurado para retornar agrupamento
        Map<String, List<String>> agrupamento = Map.of(
                "Eletrônicos", List.of("Notebook Gamer"),
                "Móveis", List.of("Cadeira de Escritório")
        );
        when(produtoService.buscarNomesProdutosAgrupadosPorCategoria())
                .thenReturn(agrupamento);

        // When & Then - Fazemos requisição e verificamos estrutura JSON
        given()
                .when()
                .get("/produtos/por-categoria")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("Eletrônicos", hasSize(1))
                .body("Eletrônicos[0]", equalTo("Notebook Gamer"))
                .body("Móveis", hasSize(1))
                .body("Móveis[0]", equalTo("Cadeira de Escritório"));

        verify(produtoService, times(1)).buscarNomesProdutosAgrupadosPorCategoria();
    }
}