package br.ufal.ic.p2.wepayu.exceptions;

public class SalaryNotPositiveException extends Exception {
    public SalaryNotPositiveException() {
        super("Salario deve ser nao-negativo.");
    }
}
