package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeNotInSyndicateException extends Exception {
    public EmployeeNotInSyndicateException(){
        super("Empregado nao eh sindicalizado.");
    }
}
