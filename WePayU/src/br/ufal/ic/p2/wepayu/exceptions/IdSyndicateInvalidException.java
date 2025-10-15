package br.ufal.ic.p2.wepayu.exceptions;

public class IdSyndicateInvalidException extends Exception {
    public IdSyndicateInvalidException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }
}
