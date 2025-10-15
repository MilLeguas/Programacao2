package br.ufal.ic.p2.wepayu.exceptions;

public class CommissionNotNumberException extends Exception {
    public CommissionNotNumberException() {
        super("Comissao deve ser numerica.");
    }
}
