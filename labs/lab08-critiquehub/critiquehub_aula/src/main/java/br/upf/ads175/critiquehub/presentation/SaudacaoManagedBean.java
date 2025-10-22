package br.upf.ads175.critiquehub.presentation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class SaudacaoManagedBean {

    private Saudacao saudacao = new Saudacao();

    public Saudacao getSaudacao() {
        return saudacao;
    }

    public void limpar() {
        saudacao = new Saudacao();
    }
}
