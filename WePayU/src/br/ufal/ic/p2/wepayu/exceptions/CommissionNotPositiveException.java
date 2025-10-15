package br.ufal.ic.p2.wepayu.exceptions;

public class CommissionNotPositiveException extends Exception {
    public CommissionNotPositiveException() {
        super("Comissao deve ser nao-negativa.");
    }
}
