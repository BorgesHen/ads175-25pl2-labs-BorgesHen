package com.biblioteca.repository;

import com.biblioteca.entity.Autor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class AutorRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Autor> findAll() {
        return entityManager.createQuery("SELECT a FROM Autor a", Autor.class).getResultList();
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(a) FROM Autor a", Long.class).getSingleResult();
    }
}
