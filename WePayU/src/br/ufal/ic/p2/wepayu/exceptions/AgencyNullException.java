package br.ufal.ic.p2.wepayu.exceptions;

public class AgencyNullException extends Exception {
    public AgencyNullException() {
        super("Agencia nao pode ser nulo.");
    }
}
