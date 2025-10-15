package br.ufal.ic.p2.wepayu.exceptions;

public class ValueNotBooleanException extends Exception {
    public ValueNotBooleanException(){
        super("Valor deve ser true ou false.");
    }
}
