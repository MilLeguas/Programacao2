package br.ufal.ic.p2.wepayu.exceptions;

public class ValueNullException extends Exception {
    public ValueNullException() {
        super("Valor nao pode ser nulo.");
    }
}
