package br.ufal.ic.p2.wepayu.exceptions;

public class ValueNotPositiveException extends Exception {
    public ValueNotPositiveException() {
        super("Valor deve ser positivo.");
    }
}
