package br.ufal.ic.p2.wepayu.exceptions;

public class EmployeeNotHourlyException extends Exception {
    public EmployeeNotHourlyException(){
        super("Empregado nao eh horista.");
    }
}
