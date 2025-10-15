package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeNullException extends Exception {
    public EmployeeNullException() {
        super("Identificacao do empregado nao pode ser nula.");
    }
}
