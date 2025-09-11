package br.ufal.ic.p2.wepayu.exceptions;

public class SalaryNotNumberException extends Exception {
    public SalaryNotNumberException() {
        super("Salario deve ser numerico.");
    }
}
