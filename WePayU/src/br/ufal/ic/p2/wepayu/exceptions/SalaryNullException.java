package br.ufal.ic.p2.wepayu.exceptions;

public class SalaryNullException extends Exception {
    public SalaryNullException() {
        super("Salario nao pode ser nulo.");
    }
}
