package br.ufal.ic.p2.wepayu.exceptions;

public class MemberNotFoundException extends Exception {
    public MemberNotFoundException() {
        super("Membro nao existe.");
    }
}
