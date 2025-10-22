package br.upf.ads175.critiquehub.exception;

/**
 * Exceção lançada quando há tentativa de criar dados que violam restrições de unicidade.
 */
public class DadosDuplicadosException extends RuntimeException {

    public DadosDuplicadosException(String mensagem) {
        super(mensagem);
    }

    public DadosDuplicadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
