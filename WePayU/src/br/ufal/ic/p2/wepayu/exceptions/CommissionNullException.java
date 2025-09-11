package br.ufal.ic.p2.wepayu.exceptions;

public class CommissionNullException extends Exception {
    public CommissionNullException() {
        super("Comissao nao pode ser nula.");
    }
}
