package br.ufal.ic.p2.wepayu.exceptions;

public class ValueNotNumberException extends Exception {
    public ValueNotNumberException() {
        super("Valor deve ser numerico.");
    }
}
