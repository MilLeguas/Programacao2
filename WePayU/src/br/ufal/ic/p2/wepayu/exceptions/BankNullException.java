package br.ufal.ic.p2.wepayu.exceptions;

public class BankNullException extends Exception {
    public BankNullException() {
        super("Banco nao pode ser nulo.");
    }
}
