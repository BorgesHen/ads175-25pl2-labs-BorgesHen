package main.java.br.upf.ads175.service;

import main.java.br.upf.ads175.dto.CategoriaDTO;
import main.java.br.upf.ads175.dto.ProdutoDTO;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoService {

    /**
     * Simula nosso "banco de dados" por enquanto
     */
    private List<ProdutoDTO> obterProdutosMock() {
        return List.of(
            new ProdutoDTO(1L, "Notebook Gamer", new BigDecimal("8500.00"), true, new CategoriaDTO("Eletrônicos")),
            new ProdutoDTO(2L, "Cadeira de Escritório", new BigDecimal("1200.50"), true, new CategoriaDTO("Móveis")),
            new ProdutoDTO(3L, "Monitor 4K", new BigDecimal("2300.00"), false, new CategoriaDTO("Eletrônicos")),
            new ProdutoDTO(4L, "Mesa de Escritório", new BigDecimal("850.00"), true, new CategoriaDTO("Móveis")),
            new ProdutoDTO(5L, "Teclado Mecânico", new BigDecimal("450.00"), true, new CategoriaDTO("Eletrônicos")),
            new ProdutoDTO(6L, "Smartphone Premium", new BigDecimal("3500.00"), true, new CategoriaDTO("Eletrônicos"))
        );
    }

    /**
     * Retorna apenas os produtos ativos, ordenados por nome.
     */
    public List<ProdutoDTO> buscarProdutosAtivosOrdenadosPorNome() {
        return obterProdutosMock().stream()
            .filter(ProdutoDTO::ativo)
            .sorted(Comparator.comparing(ProdutoDTO::nome))
            .toList();
    }

    /**
     * Busca um produto por ID usando Optional.
     */
    public Optional<ProdutoDTO> buscarPorId(Long id) {
        return obterProdutosMock().stream()
            .filter(produto -> produto.id().equals(id))
            .findFirst();
    }

    /**
     * Agrupa os nomes dos produtos por sua categoria.
     */
    public Map<String, List<String>> buscarNomesProdutosAgrupadosPorCategoria() {
        return obterProdutosMock().stream()
            .collect(Collectors.groupingBy(
                produto -> produto.categoria().nome(),
                Collectors.mapping(ProdutoDTO::nome, Collectors.toList())
            ));
    }

    /**
     * Retorna apenas produtos premium (preço > 1000) e ativos, ordenados por preço decrescente.
     */
    public List<ProdutoDTO> buscarProdutosPremium() {
        return obterProdutosMock().stream()
            .filter(ProdutoDTO::ativo)
            .filter(ProdutoDTO::isPremium)
            .sorted(Comparator.comparing(ProdutoDTO::preco).reversed())
            .toList();
    }

    /**
     * Calcula estatísticas dos produtos por categoria.
     */
    public Map<String, Map<String, Object>> obterEstatisticasProdutosPorCategoria() {
        return obterProdutosMock().stream()
            .filter(ProdutoDTO::ativo)
            .collect(Collectors.groupingBy(
                produto -> produto.categoria().nome(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    produtos -> Map.of(
                        "quantidade", produtos.size(),
                        "precoMedio", produtos.stream()
                            .map(ProdutoDTO::preco)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(produtos.size())),
                        "precoMaximo", produtos.stream()
                            .map(ProdutoDTO::preco)
                            .max(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO)
                    )
                )
            ));
    }
}