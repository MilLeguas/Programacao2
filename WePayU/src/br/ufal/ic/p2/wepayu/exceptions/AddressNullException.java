package br.ufal.ic.p2.wepayu.exceptions;

public class AddressNullException extends Exception {
    public AddressNullException() {
        super("Endereco nao pode ser nulo.");
    }
}
