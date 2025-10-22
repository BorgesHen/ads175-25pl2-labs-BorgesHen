package br.upf.ads175.critiquehub.repository;

import br.upf.ads175.critiquehub.entity.model.Tag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade Tag.
 */
@ApplicationScoped
public class TagRepository implements PanacheRepository<Tag> {

    /**
     * Busca uma tag por nome exato (case insensitive).
     *
     * @param nome nome da tag
     * @return tag encontrada ou empty
     */
    public Optional<Tag> findByNome(String nome) {
        return find("LOWER(nome) = LOWER(?1)", nome).firstResultOptional();
    }

    /**
     * Busca uma tag ativa por nome exato (case insensitive).
     *
     * @param nome nome da tag
     * @return tag ativa encontrada ou empty
     */
    public Optional<Tag> findByNomeAtiva(String nome) {
        return find("LOWER(nome) = LOWER(?1) AND ativo = true", nome).firstResultOptional();
    }

    /**
     * Lista todas as tags ativas ordenadas por nome.
     *
     * @return lista de tags ativas
     */
    public List<Tag> findAllAtivas() {
        return find("ativo = true ORDER BY nome").list();
    }

    /**
     * Lista todas as tags inativas ordenadas por nome.
     *
     * @return lista de tags inativas
     */
    public List<Tag> findAllInativas() {
        return find("ativo = false ORDER BY nome").list();
    }

    /**
     * Busca tags por nome parcial (case insensitive).
     *
     * @param termo termo de busca
     * @return lista de tags que contêm o termo
     */
    public List<Tag> findByNomeParcial(String termo) {
        return find("LOWER(nome) LIKE LOWER(?1) AND ativo = true ORDER BY nome",
                   "%" + termo + "%").list();
    }

    /**
     * Lista as tags mais utilizadas.
     *
     * @param limite número máximo de resultados
     * @return lista das tags mais utilizadas
     */
    public List<Tag> findMaisUsadas(int limite) {
        return find("ativo = true AND SIZE(itens) > 0 ORDER BY SIZE(itens) DESC")
                .page(0, limite)
                .list();
    }

    /**
     * Lista tags não utilizadas (sem itens associados).
     *
     * @return lista de tags não utilizadas
     */
    public List<Tag> findNaoUtilizadas() {
        return find("ativo = true AND SIZE(itens) = 0 ORDER BY nome").list();
    }

    /**
     * Lista tags utilizadas (com pelo menos um item associado).
     *
     * @return lista de tags utilizadas
     */
    public List<Tag> findUtilizadas() {
        return find("ativo = true AND SIZE(itens) > 0 ORDER BY nome").list();
    }

    /**
     * Conta o total de tags ativas.
     *
     * @return número de tags ativas
     */
    public long countAtivas() {
        return count("ativo = true");
    }

    /**
     * Conta o total de tags inativas.
     *
     * @return número de tags inativas
     */
    public long countInativas() {
        return count("ativo = false");
    }

    /**
     * Conta tags não utilizadas.
     *
     * @return número de tags sem itens associados
     */
    public long countNaoUtilizadas() {
        return count("ativo = true AND SIZE(itens) = 0");
    }

    /**
     * Verifica se existe uma tag com o nome especificado.
     *
     * @param nome nome da tag
     * @return true se existe, false caso contrário
     */
    public boolean existsByNome(String nome) {
        return count("LOWER(nome) = LOWER(?1)", nome) > 0;
    }

    /**
     * Verifica se existe uma tag ativa com o nome especificado.
     *
     * @param nome nome da tag
     * @return true se existe, false caso contrário
     */
    public boolean existsByNomeAtiva(String nome) {
        return count("LOWER(nome) = LOWER(?1) AND ativo = true", nome) > 0;
    }

    /**
     * Verifica se existe outra tag com o mesmo nome (para validação de updates).
     *
     * @param nome nome da tag
     * @param idExcluir ID da tag a ser excluída da verificação
     * @return true se existe outra tag com o mesmo nome
     */
    public boolean existsOutraTagComNome(String nome, Long idExcluir) {
        return count("LOWER(nome) = LOWER(?1) AND id != ?2", nome, idExcluir) > 0;
    }

    /**
     * Remove todas as tags não utilizadas.
     *
     * @return número de tags removidas
     */
    public long deleteNaoUtilizadas() {
        return delete("ativo = true AND SIZE(itens) = 0");
    }

    /**
     * Inativa todas as tags não utilizadas.
     *
     * @return número de tags inativadas
     */
    public long inativarNaoUtilizadas() {
        return update("ativo = false WHERE ativo = true AND SIZE(itens) = 0");
    }

    /**
     * Busca tags por cor.
     *
     * @param cor cor em formato hexadecimal
     * @return lista de tags com a cor especificada
     */
    public List<Tag> findByCor(String cor) {
        return find("cor = ?1 AND ativo = true ORDER BY nome", cor).list();
    }
}
