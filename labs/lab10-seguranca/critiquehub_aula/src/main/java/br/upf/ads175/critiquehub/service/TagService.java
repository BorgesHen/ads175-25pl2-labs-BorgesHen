package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.model.Tag;
import br.upf.ads175.critiquehub.entity.model.ItemCultural;
import br.upf.ads175.critiquehub.exception.DadosDuplicadosException;
import br.upf.ads175.critiquehub.exception.EntidadeNaoEncontradaException;
import br.upf.ads175.critiquehub.exception.RegraDeNegocioException;
import br.upf.ads175.critiquehub.repository.TagRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Serviço responsável pelas operações de negócio relacionadas às Tags.
 */
@ApplicationScoped
@Transactional
public class TagService {

    @Inject
    TagRepository tagRepository;

    /**
     * Cria uma nova tag.
     *
     * @param tag tag a ser criada
     * @return tag criada
     * @throws BusinessException se já existir tag com o mesmo nome
     */
    public Tag criar(@Valid @NotNull Tag tag) {
        validarTagUnica(tag.getNome(), null);
        tagRepository.persist(tag);
        return tag;
    }

    /**
     * Cria uma nova tag pelo nome.
     *
     * @param nome nome da tag
     * @return tag criada
     * @throws BusinessException se já existir tag com o mesmo nome
     */
    public Tag criar(@NotBlank String nome) {
        return criar(new Tag(nome));
    }

    /**
     * Cria uma nova tag com nome e descrição.
     *
     * @param nome      nome da tag
     * @param descricao descrição da tag
     * @return tag criada
     * @throws BusinessException se já existir tag com o mesmo nome
     */
    public Tag criar(@NotBlank String nome, String descricao) {
        return criar(new Tag(nome, descricao));
    }

    /**
     * Atualiza uma tag existente.
     *
     * @param id  ID da tag
     * @param tag dados atualizados da tag
     * @return tag atualizada
     * @throws EntityNotFoundException se a tag não for encontrada
     * @throws BusinessException       se já existir outra tag com o mesmo nome
     */
    public Tag atualizar(@NotNull Long id, @Valid @NotNull Tag tag) {
        Tag tagExistente = buscarPorId(id);

        validarTagUnica(tag.getNome(), id);

        tagExistente.setNome(tag.getNome());
        tagExistente.setCor(tag.getCor());
        tagExistente.setDescricao(tag.getDescricao());

        return tagExistente;
    }

    /**
     * Busca uma tag por ID.
     *
     * @param id ID da tag
     * @return tag encontrada
     * @throws EntidadeNaoEncontradaException se a tag não for encontrada
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Tag buscarPorId(@NotNull Long id) {
        return tagRepository.findByIdOptional(id)
            .orElseThrow(() -> new EntidadeNaoEncontradaException("Tag não encontrada com ID: " + id));
    }

    /**
     * Busca uma tag por nome.
     *
     * @param nome nome da tag
     * @return tag encontrada ou empty se não existir
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<Tag> buscarPorNome(@NotBlank String nome) {
        String nomeNormalizado = nome.toLowerCase().trim();
        return tagRepository.find("LOWER(nome) = ?1 AND ativo = true", nomeNormalizado).firstResultOptional();
    }

    /**
     * Busca ou cria uma tag pelo nome.
     *
     * @param nome nome da tag
     * @return tag existente ou nova tag criada
     */
    public Tag buscarOuCriar(@NotBlank String nome) {
        return buscarPorNome(nome)
            .orElseGet(() -> criar(nome));
    }

    /**
     * Lista todas as tags ativas.
     *
     * @return lista de tags ativas
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Tag> listarAtivas() {
        return tagRepository.find("ativo = true ORDER BY nome").list();
    }

    /**
     * Lista todas as tags (ativas e inativas).
     *
     * @return lista de todas as tags
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Tag> listarTodas() {
        return tagRepository.findAll().list();
    }

    /**
     * Busca tags por nome (busca parcial).
     *
     * @param nome termo de busca
     * @return lista de tags que contêm o termo no nome
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Tag> buscarPorNomeParcial(@NotBlank String nome) {
        String termoBusca = "%" + nome.toLowerCase().trim() + "%";
        return tagRepository.find("LOWER(nome) LIKE ?1 AND ativo = true ORDER BY nome", termoBusca).list();
    }

    /**
     * Lista as tags mais utilizadas.
     *
     * @param limite número máximo de tags a retornar
     * @return lista das tags mais utilizadas
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Tag> listarMaisUsadas(int limite) {
        return tagRepository.find("ativo = true AND SIZE(itens) > 0 ORDER BY SIZE(itens) DESC")
            .page(0, limite)
            .list();
    }

    /**
     * Lista tags não utilizadas.
     *
     * @return lista de tags sem itens associados
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Tag> listarNaoUtilizadas() {
        return tagRepository.find("ativo = true AND SIZE(itens) = 0 ORDER BY nome").list();
    }

    /**
     * Ativa uma tag.
     *
     * @param id ID da tag
     * @return tag ativada
     * @throws EntityNotFoundException se a tag não for encontrada
     */
    public Tag ativar(@NotNull Long id) {
        Tag tag = buscarPorId(id);
        tag.setAtivo(true);
        return tag;
    }

    /**
     * Inativa uma tag.
     *
     * @param id ID da tag
     * @return tag inativada
     * @throws EntidadeNaoEncontradaException se a tag não for encontrada
     */
    public Tag inativar(@NotNull Long id) {
        Tag tag = buscarPorId(id);
        tag.setAtivo(false);
        return tag;
    }

    /**
     * Remove uma tag permanentemente.
     * Só permite remoção se a tag não estiver associada a nenhum item.
     *
     * @param id ID da tag
     * @throws EntidadeNaoEncontradaException se a tag não for encontrada
     * @throws RegraDeNegocioException       se a tag estiver em uso
     */
    public void remover(@NotNull Long id) {
        Tag tag = buscarPorId(id);

        if (!tag.getItens().isEmpty()) {
            throw new RegraDeNegocioException("Não é possível remover a tag '" + tag.getNome() +
                "' pois ela está associada a " + tag.getItens().size() + " item(ns) cultural(is)");
        }

        tagRepository.delete(tag);
    }

    /**
     * Remove tags não utilizadas.
     *
     * @return número de tags removidas
     */
    public long limparTagsNaoUtilizadas() {
        List<Tag> tagsNaoUtilizadas = listarNaoUtilizadas();

        for (Tag tag : tagsNaoUtilizadas) {
            tagRepository.delete(tag);
        }

        return tagsNaoUtilizadas.size();
    }

    /**
     * Associa uma tag a um item cultural.
     *
     * @param tagId ID da tag
     * @param item  item cultural
     * @throws EntidadeNaoEncontradaException se a tag não for encontrada
     */
    public void associarAoItem(@NotNull Long tagId, @NotNull ItemCultural item) {
        Tag tag = buscarPorId(tagId);
        item.adicionarTag(tag);
    }

    /**
     * Remove a associação de uma tag com um item cultural.
     *
     * @param tagId ID da tag
     * @param item  item cultural
     * @throws EntidadeNaoEncontradaException se a tag não for encontrada
     */
    public void removerDoItem(@NotNull Long tagId, @NotNull ItemCultural item) {
        Tag tag = buscarPorId(tagId);
        item.removerTag(tag);
    }

    /**
     * Conta o total de tags ativas.
     *
     * @return número de tags ativas
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public long contarAtivas() {
        return tagRepository.count("ativo = true");
    }

    /**
     * Conta o total de tags.
     *
     * @return número total de tags
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public long contarTodas() {
        return tagRepository.count();
    }

    /**
     * Valida se o nome da tag é único no sistema.
     *
     * @param nome nome da tag
     * @param idExcluir ID da tag a ser excluída da validação (para updates)
     * @throws DadosDuplicadosException se já existir uma tag com o mesmo nome
     */
    private void validarTagUnica(String nome, Long idExcluir) {
        String nomeNormalizado = nome.toLowerCase().trim();

        Optional<Tag> tagExistente = tagRepository.find("LOWER(nome) = ?1", nomeNormalizado)
            .firstResultOptional();

        if (tagExistente.isPresent() && !tagExistente.get().getId().equals(idExcluir)) {
            throw new DadosDuplicadosException("Já existe uma tag com o nome '" + nome + "'");
        }
    }
}
