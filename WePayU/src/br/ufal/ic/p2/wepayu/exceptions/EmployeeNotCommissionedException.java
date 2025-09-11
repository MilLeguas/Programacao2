package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeNotCommissionedException extends Exception {
    public EmployeeNotCommissionedException(){
        super("Empregado nao eh comissionado.");
    }
}
