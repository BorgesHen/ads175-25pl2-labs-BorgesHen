package br.upf.ads175.critiquehub.presentation;

import java.io.Serializable;
import java.util.List;

import br.upf.ads175.critiquehub.entity.model.Filme;
import br.upf.ads175.critiquehub.service.FilmeService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class FilmeController implements Serializable {

    private Filme filme;
    private List<Filme> listaFilmes;

    @Inject
    private FilmeService filmeService;

    @PostConstruct
    public void init() {
        filme = new Filme();
        carregarFilmes();
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public List<Filme> getListaFilmes() {
        System.out.println(listaFilmes);
        
        return listaFilmes;
    }

    public void salvar() {
        try {
            filmeService.salvar(filme);
            novo();
            carregarFilmes();
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Filme salvo!");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro!" + e.getMessage(), "");
        }
    }

    public void carregarFilmes() {
        try {
            this.listaFilmes = filmeService.listarTodos();
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro!" + e.getMessage(), "");
        }
    }

    public void novo() {
        filme = new Filme();
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}
