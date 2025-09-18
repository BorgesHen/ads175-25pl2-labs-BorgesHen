package br.upf.ads175.critiquehub.exception;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }

    public RegraDeNegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
