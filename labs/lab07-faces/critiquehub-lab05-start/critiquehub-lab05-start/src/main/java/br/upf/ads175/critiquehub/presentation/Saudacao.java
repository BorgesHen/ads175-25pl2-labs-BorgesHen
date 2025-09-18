package br.upf.ads175.critiquehub.presentation;

// Saudacao.java
/**
 * Bean de modelo para armazenar dados da saudação.
 * Este é um POJO (Plain Old Java Object) simples que representa
 * os dados que serão manipulados na interface.
 */
public class Saudacao {
    private String nome;      // Nome do usuário
    private String mensagem;  // Mensagem de saudação

    /**
     * Construtor padrão necessário para JSF
     */
    public Saudacao() {
        // Construtor vazio obrigatório
    }

    // Getter e setter para nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter e setter para mensagem
    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    /**
     * Método auxiliar para exibição formatada
     */
    public String getSaudacaoCompleta() {
        if (nome != null && mensagem != null &&
                !nome.trim().isEmpty() && !mensagem.trim().isEmpty()) {
            return nome + ", " + mensagem;
        }
        return "";
    }
}
