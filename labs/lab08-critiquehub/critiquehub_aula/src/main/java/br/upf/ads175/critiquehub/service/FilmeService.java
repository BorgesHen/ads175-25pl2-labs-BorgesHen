package br.upf.ads175.critiquehub.service;

import java.util.ArrayList;
import java.util.List;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import br.upf.ads175.critiquehub.entity.model.Filme;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class FilmeService {
    
    @Inject
    private EntityManager em;

    private List<Filme> lista = new ArrayList<>();

    public List<Filme> listarTodos() {
        //return em.createNamedQuery("Filme.listarTodos", Filme.class).getResultList();

        return lista;
    }

    
    public void salvar(Filme filme) {
        //em.persist(filme);

        lista.add(filme);

        System.out.println(filme.getTitulo());
    }

}
