package br.ufal.ic.p2.wepayu.exceptions;

public class RedoEmptyException extends Exception {
    public RedoEmptyException() {
        super("Nao ha comando a refazer.");
    }
}
