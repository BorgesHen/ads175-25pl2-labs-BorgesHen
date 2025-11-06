package com.biblioteca.service;

import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Livro;
import com.biblioteca.entity.Emprestimo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class BibliotecaService {

    @PersistenceContext
    EntityManager em;

    // Listagens
    public List<Autor> listarAutores() {
        return em.createQuery("SELECT a FROM Autor a ORDER BY a.nome", Autor.class)
                 .getResultList();
    }

    public List<Livro> listarLivros() {
        // join fetch autor para evitar LazyInitialization nas páginas
        return em.createQuery("SELECT l FROM Livro l LEFT JOIN FETCH l.autor ORDER BY l.titulo", Livro.class)
                 .getResultList();
    }

    public List<Emprestimo> listarEmprestimosAtivos() {
        // Considera ativo quando dataDevolucao é nula ou campo 'ativo' = true, conforme existir no modelo
        // Tenta a primeira opção e caso não exista, a segunda (ajuste conforme seu modelo)
        try {
            return em.createQuery("SELECT e FROM Emprestimo e LEFT JOIN FETCH e.livro l LEFT JOIN FETCH l.autor WHERE e.dataDevolucao IS NULL ORDER BY e.dataEmprestimo DESC", Emprestimo.class)
                     .getResultList();
        } catch (IllegalArgumentException ex) {
            return em.createQuery("SELECT e FROM Emprestimo e LEFT JOIN FETCH e.livro l LEFT JOIN FETCH l.autor WHERE e.ativo = true ORDER BY e.dataEmprestimo DESC", Emprestimo.class)
                     .getResultList();
        }
    }

    // Contagens
    public long contarLivros() {
        Long c = em.createQuery("SELECT COUNT(l) FROM Livro l", Long.class).getSingleResult();
        return c != null ? c : 0L;
    }

    public long contarLivrosDisponiveis() {
        try {
            Long c = em.createQuery("SELECT COUNT(l) FROM Livro l WHERE l.disponivel = true", Long.class).getSingleResult();
            return c != null ? c : 0L;
        } catch (Exception ex) {
            return 0L;
        }
    }

    public long contarEmprestimosAtivos() {
        try {
            Long c = em.createQuery("SELECT COUNT(e) FROM Emprestimo e WHERE e.dataDevolucao IS NULL", Long.class).getSingleResult();
            return c != null ? c : 0L;
        } catch (Exception ex) {
            try {
                Long c2 = em.createQuery("SELECT COUNT(e) FROM Emprestimo e WHERE e.ativo = true", Long.class).getSingleResult();
                return c2 != null ? c2 : 0L;
            } catch (Exception ex2) {
                return 0L;
            }
        }
    }

    public long contarAutores() {
        Long c = em.createQuery("SELECT COUNT(a) FROM Autor a", Long.class).getSingleResult();
        return c != null ? c : 0L;
    }
}
