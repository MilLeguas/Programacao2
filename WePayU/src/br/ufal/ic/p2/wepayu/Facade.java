package br.ufal.ic.p2.wepayu;

import br.ufal.ic.p2.wepayu.exceptions.*;
import br.ufal.ic.p2.wepayu.services.Services;

public class Facade {
    private final Services services;

    public Facade() throws Exception {
        Services.undo.clear();
        Services.redo.clear();
        this.services = new Services();
    }

    private void checarSistema() throws Exception {
        if (services.isSistemaEncerrado()) {
            throw new CommandInvalidException();
        }
    }

    public int getNumeroDeEmpregados() throws Exception {
        checarSistema();
        return services.getNumeroDeEmpregados();
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        checarSistema();
        return services.criarEmpregado(nome, endereco, tipo, salario);
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        checarSistema();
        return services.criarEmpregado(nome, endereco, tipo, salario, comissao);
    }

    public void removerEmpregado(String id) throws Exception {
        checarSistema();
        services.removerEmpregado(id);
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        checarSistema();
        return services.getAtributoEmpregado(id, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        checarSistema();
        return services.getEmpregadoPorNome(nome, indice);
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws  Exception {
        checarSistema();
        services.alteraEmpregado(id, atributo, valor);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String comissao) throws  Exception {
        checarSistema();
        services.alteraEmpregado(id, atributo, valor, comissao);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato, String taxaSindical) throws  Exception {
        checarSistema();
        services.alteraEmpregado(id, atributo, valor, idSindicato, taxaSindical);
    }

    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia, String contaCorrente) throws  Exception {
        checarSistema();
        services.alteraEmpregado(id, atributo, valor, banco, agencia, contaCorrente);
    }

    public void lancaCartao(String id, String data, String horas) throws Exception{
        checarSistema();
        services.lancaCartao(id, data, horas);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return services.getHorasNormaisTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return services.getHorasExtrasTrabalhadas(id, dataInicial, dataFinal);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception {
        checarSistema();
        services.lancaVenda(id, data, valor);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return services.getVendasRealizadas(id, dataInicial, dataFinal);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws Exception{
        checarSistema();
        services.lancaTaxaServico(membro, data, valor);
    }

    public String getTaxasServico(String membro, String dataInicial, String dataFinal) throws Exception {
        checarSistema();
        return services.getTaxasServico(membro, dataInicial, dataFinal);
    }

    public String totalFolha(String data) throws Exception {
        checarSistema();
        return services.totalFolha(data);
    }

    public void rodaFolha(String data, String saida) throws Exception {
        checarSistema();
        services.rodaFolha(data, saida);
    }

    public void undo() throws Exception {
        checarSistema();
        services.undo();
    }

    public void redo() throws Exception {
        checarSistema();
        services.redo();
    }

    public void zerarSistema() {
        services.zerarSistema();
    }

    public void encerrarSistema() throws Exception {
        services.encerrarSistema();
    }
}