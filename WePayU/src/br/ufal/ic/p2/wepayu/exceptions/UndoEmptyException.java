package br.ufal.ic.p2.wepayu.exceptions;

public class UndoEmptyException extends Exception {
    public UndoEmptyException() {
        super("Nao ha comando a desfazer.");
    }
}
