package br.ufal.ic.p2.wepayu.exceptions;

public class BankNotFoundException extends Exception {
    public BankNotFoundException(){
        super("Empregado nao recebe em banco.");
    }
}
