// SaudacaoController.java
package br.upf.ads175.critiquehub.presentation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Bean gerenciado para aplicação de saudação com PrimeFaces.
 */
@Named
@RequestScoped
public class SaudacaoController {

    private String nome;
    private String email;
    private String mensagem = "";

    /**
     * Gera uma saudação personalizada.
     */
    public void gerarSaudacao() {
        if (nome != null && !nome.trim().isEmpty()) {
            mensagem = "Olá, " + nome + "! Bem-vindo ao sistema CritiqueHub!";

            if (email != null && !email.trim().isEmpty()) {
                mensagem += " Seu email " + email + " foi registrado.";
            }

            // Adiciona mensagem de sucesso
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                               "Sucesso!",
                               "Saudação gerada com sucesso!"));
        } else {
            mensagem = "";
            // Adiciona mensagem de erro
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                               "Erro!",
                               "Por favor, digite seu nome!"));
        }
    }

    /**
     * Limpa todos os campos.
     */
    public void limpar() {
        nome = "";
        email = "";
        mensagem = "";

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
                           "Limpo!",
                           "Campos foram limpos!"));
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public boolean isMensagemVazia() {
        return mensagem == null || mensagem.trim().isEmpty();
    }
}
