package br.upf.ads175.critiquehub.service;

import br.upf.ads175.critiquehub.entity.model.Avaliacao;
import br.upf.ads175.critiquehub.entity.model.Comentario;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ComentarioService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Cria um comentário principal em uma avaliação
     */
    @Transactional
    public Comentario criarComentario(Long avaliacaoId, Long autorId, String conteudo) {
        Avaliacao avaliacao = buscarAvaliacaoOuFalhar(avaliacaoId);
        Usuario autor = buscarUsuarioOuFalhar(autorId);

        Comentario comentario = new Comentario(avaliacao, autor, conteudo);
        entityManager.persist(comentario);
        return comentario;
    }

    /**
     * Cria uma resposta a um comentário existente
     */
    @Transactional
    public Comentario responderComentario(Long comentarioPaiId, Long autorId, String conteudo) {
        Comentario comentarioPai = buscarComentarioOuFalhar(comentarioPaiId);
        Usuario autor = buscarUsuarioOuFalhar(autorId);

        // Verificar se é um comentário principal
        if (!comentarioPai.isComentarioPrincipal()) {
            throw new IllegalArgumentException("Só é poss��vel responder comentários principais");
        }

        Comentario resposta = new Comentario(comentarioPai.getAvaliacao(), autor, conteudo, comentarioPai);
        entityManager.persist(resposta);
        return resposta;
    }

    /**
     * Carrega todos os comentários de uma avaliação com suas respostas
     */
    public List<Comentario> carregarComentariosComRespostas(Long avaliacaoId) {
        return entityManager.createQuery(
            "SELECT DISTINCT c FROM Comentario c " +
            "LEFT JOIN FETCH c.respostas r " +
            "LEFT JOIN FETCH c.autor " +
            "LEFT JOIN FETCH r.autor " +
            "WHERE c.avaliacao.id = :avaliacaoId " +
            "AND c.comentarioPai IS NULL " + // Apenas comentários principais
            "ORDER BY c.dataComentario ASC",
            Comentario.class)
            .setParameter("avaliacaoId", avaliacaoId)
            .getResultList();
    }

    /**
     * Carrega apenas comentários principais (sem respostas)
     */
    public List<Comentario> carregarComentariosPrincipais(Long avaliacaoId) {
        return entityManager.createQuery(
            "SELECT c FROM Comentario c " +
            "JOIN FETCH c.autor " +
            "WHERE c.avaliacao.id = :avaliacaoId " +
            "AND c.comentarioPai IS NULL " +
            "ORDER BY c.dataComentario ASC",
            Comentario.class)
            .setParameter("avaliacaoId", avaliacaoId)
            .getResultList();
    }

    /**
     * Conta total de comentários de uma avaliação
     */
    public Long contarComentarios(Long avaliacaoId) {
        return entityManager.createQuery(
            "SELECT COUNT(c) FROM Comentario c " +
            "WHERE c.avaliacao.id = :avaliacaoId",
            Long.class)
            .setParameter("avaliacaoId", avaliacaoId)
            .getSingleResult();
    }

    /**
     * Remove um comentário (e suas respostas automaticamente)
     */
    @Transactional
    public boolean removerComentario(Long comentarioId, Long autorId) {
        Comentario comentario = entityManager.find(Comentario.class, comentarioId);

        if (comentario == null) {
            return false;
        }

        // Verificar se o usuário é o autor
        if (!comentario.getAutor().getId().equals(autorId)) {
            throw new SecurityException("Usuário não pode remover comentário de outro usuário");
        }

        entityManager.remove(comentario);
        return true;
    }

    // Métodos auxiliares
    private Avaliacao buscarAvaliacaoOuFalhar(Long id) {
        Avaliacao avaliacao = entityManager.find(Avaliacao.class, id);
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação não encontrada: " + id);
        }
        return avaliacao;
    }

    private Usuario buscarUsuarioOuFalhar(Long id) {
        Usuario usuario = entityManager.find(Usuario.class, id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado: " + id);
        }
        return usuario;
    }

    private Comentario buscarComentarioOuFalhar(Long id) {
        Comentario comentario = entityManager.find(Comentario.class, id);
        if (comentario == null) {
            throw new IllegalArgumentException("Comentário não encontrado: " + id);
        }
        return comentario;
    }
}
