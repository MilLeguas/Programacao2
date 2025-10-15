package br.ufal.ic.p2.wepayu.exceptions;

public class IdSyndicateNullException extends Exception {
    public IdSyndicateNullException() {
        super("Identificacao do sindicato nao pode ser nula.");
    }
}
