package br.ufal.ic.p2.wepayu.exceptions;

public class HoursNotNumberException extends Exception {
    public HoursNotNumberException(){
        super("Horas devem ser numericas.");
    }
}
