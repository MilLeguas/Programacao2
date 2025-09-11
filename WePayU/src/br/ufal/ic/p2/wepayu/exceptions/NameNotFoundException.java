package br.ufal.ic.p2.wepayu.exceptions;

public class NameNotFoundException extends Exception {
    public NameNotFoundException() {
        super("Nao ha empregado com esse nome.");
    }
}
