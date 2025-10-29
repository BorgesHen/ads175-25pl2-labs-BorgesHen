
package com.biblioteca.controller;

import com.biblioteca.entity.Livro;
import com.biblioteca.service.BibliotecaService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;

@Named("bibliotecaBean")
@ViewScoped
public class BibliotecaBean implements Serializable {

    @Inject
    private BibliotecaService bibliotecaService;

    private Livro livro = new Livro();
    private List<Livro> livros;

    public void init() {
        carregarLivros();
    }

    public void carregarLivros() {
        livros = bibliotecaService.listarTodosLivros();
    }

    public void adicionarLivro() {
        try {
            bibliotecaService.adicionarLivro(livro);
            livros.add(livro);
            livro = new Livro();  // Reset the form
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Livro adicionado com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar livro", e.getMessage()));
        }
    }

    public void editarLivro(Livro livro) {
        this.livro = livro;
    }

    public void removerLivro(Livro livro) {
        try {
            bibliotecaService.removerLivro(livro);
            livros.remove(livro);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Livro removido com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao remover livro", e.getMessage()));
        }
    }

    // Getters e Setters
    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }
}
