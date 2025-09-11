package br.ufal.ic.p2.wepayu.exceptions;

public class HoursNotPositiveException extends Exception {
    public HoursNotPositiveException(){
        super("Horas devem ser positivas.");
    }
}
