package br.ufal.ic.p2.wepayu.exceptions;

public class CommandInvalidException extends Exception {
    public CommandInvalidException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
