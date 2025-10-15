package br.ufal.ic.p2.wepayu.exceptions;

public class SyndicateFeeNullException extends Exception {
    public SyndicateFeeNullException() {
        super("Taxa sindical nao pode ser nula.");
    }
}
