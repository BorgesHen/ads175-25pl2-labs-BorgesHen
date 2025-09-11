package br.upf.ads175.critiquehub;

import br.upf.ads175.critiquehub.entity.model.Avaliacao;
import br.upf.ads175.critiquehub.entity.model.Usuario;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class PersistenceIntegrationTest {

    @Inject
    EntityManager em;

    @Test
    @Transactional
    void  devePersistirAvaliacao()
    {
        Usuario usuario = new Usuario("usuario@gmail.com", "usuario", "Usuario");
        Avaliacao avaliacao = new Avaliacao();


        em.persist(usuario);

        usuario.adicionarAvaliacao(avaliacao);
        usuario.setEmail("novo@gmail.com");

        TypedQuery<Usuario> q2 = em.createQuery("select u from Usuario u", Usuario.class);
        List<Usuario> usuarios = q2.getResultList();

        TypedQuery<Avaliacao> q1 = em.createQuery("select a from Avaliacao a", Avaliacao.class);
        List<Avaliacao> avaliacoes = q1.getResultList();



        assertEquals(1, usuarios.size());
        assertEquals("novo@gmail.com", usuarios.getFirst().getEmail());
        assertEquals(1, avaliacoes.size());

    }
}
