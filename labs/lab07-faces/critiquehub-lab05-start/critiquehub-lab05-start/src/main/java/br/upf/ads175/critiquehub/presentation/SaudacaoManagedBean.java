// SaudacaoManagedBean.java
package br.upf.ads175.critiquehub.presentation;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

/**
 * Bean gerenciado para interação com a interface JSF.
 * A anotação @Named torna este bean acessível via Expression Language (EL).
 * A anotação @RequestScoped define que uma nova instância será criada
 * a cada requisição HTTP.
 */
@Named
@RequestScoped
public class SaudacaoManagedBean {

    private Saudacao saudacao = new Saudacao(); // Instância do modelo

    /**
     * Getter para o modelo.
     * Este método é chamado pelo JSF quando #{saudacaoManagedBean.saudacao}
     * é usado na página XHTML.
     */
    public Saudacao getSaudacao() {
        return saudacao;
    }

    /**
     * Método para limpar os campos do formulário.
     * Cria uma nova instância de Saudacao, efetivamente
     * limpando todos os campos.
     */
    public void limpar() {
        saudacao = new Saudacao();
        System.out.println("Formulário limpo"); // Log para debug
    }

    /**
     * Método para demonstrar processamento de dados.
     * Em uma aplicação real, aqui seria feita a persistência
     * ou chamada de serviços de negócio.
     */
    public void processar() {
        System.out.println("Processando saudação: " + saudacao.getSaudacaoCompleta());
    }
}