package br.ufal.ic.p2.wepayu.exceptions;

public class NameNullException extends Exception {
    public NameNullException() {
        super("Nome nao pode ser nulo.");
    }
}
