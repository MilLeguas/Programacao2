package br.ufal.ic.p2.wepayu.exceptions;

public class AccountNullException extends Exception {
    public AccountNullException() {
        super("Conta corrente nao pode ser nulo.");
    }
}
