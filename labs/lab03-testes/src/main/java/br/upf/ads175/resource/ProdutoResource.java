package br.upf.ads175.resource;

import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.service.ProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService service;

    @GET
    public List<ProdutoDTO> obterProdutosAtivos() {
        return service.buscarProdutosAtivosOrdenadosPorNome();
    }

    @GET
    @Path("/por-categoria")
    public Map<String, List<String>> obterProdutosAgrupados() {
        return service.buscarNomesProdutosAgrupadosPorCategoria();
    }

    @GET
    @Path("/{id}")
    public Response obterProdutoPorId(@PathParam("id") Long id) {
        return service.buscarPorId(id)
                .map(produto -> Response.ok(produto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("erro", "Produto não encontrado", "id", id))
                        .build());
    }

    @GET
    @Path("/premium")
    public List<ProdutoDTO> obterProdutosPremium() {
        return service.buscarProdutosPremium();
    }

    @GET
    @Path("/estatisticas")
    public Map<String, Map<String, Object>> obterEstatisticasProdutos() {
        return service.obterEstatisticasProdutosPorCategoria();
    }
}