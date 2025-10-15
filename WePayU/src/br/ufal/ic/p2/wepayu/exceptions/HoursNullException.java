package br.ufal.ic.p2.wepayu.exceptions;

public class HoursNullException extends Exception {
    public HoursNullException(){
        super("Horas nao podem ser nulas.");
    }
}
