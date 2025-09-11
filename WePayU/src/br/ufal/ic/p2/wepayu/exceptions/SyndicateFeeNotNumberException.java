package br.ufal.ic.p2.wepayu.exceptions;

public class SyndicateFeeNotNumberException extends Exception {
    public SyndicateFeeNotNumberException() {
        super("Taxa sindical deve ser numerica.");
    }
}
