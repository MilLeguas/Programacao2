package br.ufal.ic.p2.wepayu.exceptions;

public class AttributeNotFoundException extends Exception {
    public AttributeNotFoundException() {
        super("Atributo nao existe.");
    }
}
