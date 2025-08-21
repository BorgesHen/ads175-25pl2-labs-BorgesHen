package br.upf.ads175.service;

import br.upf.ads175.dto.ProdutoDTO;
import br.upf.ads175.repository.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutoService {

    @Inject
    ProdutoRepository repository;

    /**
     * Retorna apenas os produtos ativos, ordenados por nome.
     * Agora usa o repositório para buscar dados.
     */
    public List<ProdutoDTO> buscarProdutosAtivosOrdenadosPorNome() {
        return repository.findByAtivo(true)
                .stream()
                .sorted(Comparator.comparing(ProdutoDTO::nome))
                .toList();
    }

    /**
     * Busca um produto por ID usando Optional.
     * Delega para o repositório.
     */
    public Optional<ProdutoDTO> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Agrupa os nomes dos produtos por categoria.
     * Usa todos os produtos do repositório.
     */
    public Map<String, List<String>> buscarNomesProdutosAgrupadosPorCategoria() {
        return repository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        produto -> produto.categoria().nome(),
                        Collectors.mapping(ProdutoDTO::nome, Collectors.toList())
                ));
    }

    /**
     * Retorna apenas produtos premium (preço > 1000) e ativos, ordenados por preço decrescente.
     * Combina busca do repositório com lógica de negócio.
     */
    public List<ProdutoDTO> buscarProdutosPremium() {
        return repository.findByAtivo(true)
                .stream()
                .filter(ProdutoDTO::isPremium)
                .sorted(Comparator.comparing(ProdutoDTO::preco).reversed())
                .toList();
    }

    /**
     * Calcula estatísticas dos produtos por categoria.
     * Lógica de negócio complexa usando dados do repositório.
     */
    public Map<String, Map<String, Object>> obterEstatisticasProdutosPorCategoria() {
        return repository.findByAtivo(true)
                .stream()
                .collect(Collectors.groupingBy(
                        produto -> produto.categoria().nome(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                produtos -> Map.of(
                                        "quantidade", produtos.size(),
                                        "precoMedio", produtos.stream()
                                                .map(ProdutoDTO::preco)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                                .divide(BigDecimal.valueOf(produtos.size()), RoundingMode.HALF_UP),
                                        "precoMaximo", produtos.stream()
                                                .map(ProdutoDTO::preco)
                                                .max(BigDecimal::compareTo)
                                                .orElse(BigDecimal.ZERO)
                                )
                        )
                ));
    }

    /**
     * Novo método: Busca produtos por categoria usando repositório
     */
    public List<ProdutoDTO> buscarProdutosPorCategoria(String categoria) {
        return repository.findByCategoria(categoria)
                .stream()
                .filter(ProdutoDTO::ativo)
                .sorted(Comparator.comparing(ProdutoDTO::nome))
                .toList();
    }

    /**
     * Novo método: Busca produtos em uma faixa de preço
     */
    public List<Integer> buscarProdutosPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        if (precoMin == null || precoMax == null) {
            throw new IllegalArgumentException("Faixa de preço não pode ser nula");
        }
        if (precoMin.compareTo(precoMax) > 0) {
            throw new IllegalArgumentException("Preço mínimo não pode ser maior que o máximo");
        }

        return repository.findByPrecoBetween(precoMin, precoMax)
                .stream()
                .map(ProdutoDTO::fromEntity)
                .toList();
    }

}
