
package com.biblioteca.service;

import com.biblioteca.entity.Livro;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class BibliotecaService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Livro> listarTodosLivros() {
        return entityManager.createQuery("SELECT l FROM Livro l", Livro.class).getResultList();
    }

    public void adicionarLivro(Livro livro) {
        entityManager.persist(livro);
    }

    public void removerLivro(Livro livro) {
        entityManager.remove(entityManager.contains(livro) ? livro : entityManager.merge(livro));
    }
}
