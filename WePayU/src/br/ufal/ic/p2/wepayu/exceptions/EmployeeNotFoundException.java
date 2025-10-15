package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(){
        super("Empregado nao existe.");
    }
}
