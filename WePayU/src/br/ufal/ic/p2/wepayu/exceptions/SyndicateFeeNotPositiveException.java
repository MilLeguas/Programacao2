package br.ufal.ic.p2.wepayu.exceptions;

public class SyndicateFeeNotPositiveException extends Exception {
    public SyndicateFeeNotPositiveException() {
        super("Taxa sindical deve ser nao-negativa.");
    }
}
