package br.ufal.ic.p2.wepayu.exceptions;

public class DateLastPriorFirstException extends Exception {
    public DateLastPriorFirstException(){
        super("Data inicial nao pode ser posterior aa data final.");
    }
}
