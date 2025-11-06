package com.biblioteca.controller;

import com.biblioteca.entity.Autor;
import com.biblioteca.entity.Livro;
import com.biblioteca.entity.Emprestimo;
import com.biblioteca.service.BibliotecaService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("bibliotecaBean")
@ViewScoped
public class BibliotecaBean implements Serializable {

    @Inject
    private BibliotecaService bibliotecaService;

    private List<Autor> autores;
    private List<Livro> livros;
    private List<Emprestimo> emprestimosAtivos;

    private long totalLivros;
    private long livrosDisponiveis;
    private long emprestimosAtivosCount;
    private long totalAutores;

    @PostConstruct
    public void init() {
        carregarDados();
        carregarEstatisticas();
    }

    public void carregarDados() {
        this.autores = bibliotecaService.listarAutores();
        this.livros = bibliotecaService.listarLivros();
        this.emprestimosAtivos = bibliotecaService.listarEmprestimosAtivos();
    }

    public void carregarEstatisticas() {
        this.totalLivros = bibliotecaService.contarLivros();
        this.livrosDisponiveis = bibliotecaService.contarLivrosDisponiveis();
        this.emprestimosAtivosCount = bibliotecaService.contarEmprestimosAtivos();
        this.totalAutores = bibliotecaService.contarAutores();
    }

    public void recarregarDados() {
        init();
    }

    // Getters
    public List<Autor> getAutores() {
        return autores;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public List<Emprestimo> getEmprestimosAtivos() {
        return emprestimosAtivos;
    }

    public long getTotalLivros() {
        return totalLivros;
    }

    public long getLivrosDisponiveis() {
        return livrosDisponiveis;
    }

    public long getEmprestimosAtivosCount() {
        return emprestimosAtivosCount;
    }

    public long getTotalAutores() {
        return totalAutores;
    }
}
