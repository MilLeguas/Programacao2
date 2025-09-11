package br.ufal.ic.p2.wepayu.services;

public interface Command {
    void executar();
    void desfazer();
}
