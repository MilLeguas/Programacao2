package br.ufal.ic.p2.wepayu.exceptions;

public class MemberNullException extends Exception {
    public MemberNullException() {
        super("Identificacao do membro nao pode ser nula.");
    }
}
