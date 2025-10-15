package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.EmployeeNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Empregado {
    private String employeeId, fullName, address, employeeType, paymentMethod, bank, agency, account, idSyndicate, agendaPagamento;
    private double baseSalary, syndicateFee, debtSyndicate = 0;
    private boolean isSyndicateMember;

    private List<TaxaServico> serviceCharges = new ArrayList<>();
    public Empregado(String id, String nome, String endereco, String tipo, double salario) throws EmployeeNotFoundException {
        this.employeeId = id;
        this.fullName = nome;
        this.address = endereco;
        this.employeeType = tipo;
        this.baseSalary = salario;
        this.isSyndicateMember = false;
        this.paymentMethod = "emMaos";
        this.agendaPagamento = null;
    }
    public Empregado(Empregado other) {
        this.employeeId = other.employeeId;
        this.fullName = other.fullName;
        this.address = other.address;
        this.employeeType = other.employeeType;
        this.baseSalary = other.baseSalary;
        this.isSyndicateMember = other.isSyndicateMember;
        this.paymentMethod = other.paymentMethod;
        this.bank = other.bank;
        this.agency = other.agency;
        this.account = other.account;
        this.idSyndicate = other.idSyndicate;
        this.syndicateFee = other.syndicateFee;
        this.agendaPagamento = other.agendaPagamento;
        this.serviceCharges = new ArrayList<>(other.serviceCharges);
    }
    public Empregado() {}

    public double getTaxasServicoNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (!this.isSyndicateMember) return 0;
        return serviceCharges.stream().filter(charge -> !charge.getDataAsLocalDate().isBefore(dataInicial) && !charge.getDataAsLocalDate().isAfter(dataFinal)).mapToDouble(TaxaServico::getValor).sum();
    }
    public void lancaTaxaServico(TaxaServico taxaServico) { serviceCharges.add(taxaServico); }
    public void removerTaxaServico(TaxaServico taxaServico) { serviceCharges.remove(taxaServico); }

    public String getId() { return employeeId; }
    public void setId(String id) { this.employeeId = id; }
    public String getNome() { return fullName; }
    public void setNome(String nome) { this.fullName = nome; }
    public String getEndereco() { return address; }
    public void setEndereco(String endereco) { this.address = endereco; }
    public String getTipo() { return employeeType; }
    public void setTipo(String tipo) { this.employeeType = tipo; }
    public double getSalario() { return baseSalary; }
    public void setSalario(double salario) { this.baseSalary = salario; }
    public boolean isSindicalizado() { return isSyndicateMember; }
    public void setSindicalizado(boolean sindicalizado) { this.isSyndicateMember = sindicalizado; }
    public String getMetodoPagamento() { return paymentMethod; }
    public void setMetodoPagamento(String metodoPagamento) { this.paymentMethod = metodoPagamento; }
    public String getBanco() { return bank; }
    public void setBanco(String banco) { this.bank = banco; }
    public String getAgencia() { return agency; }
    public void setAgencia(String agencia) { this.agency = agencia; }
    public String getContaCorrente() { return account; }
    public void setContaCorrente(String contaCorrente) { this.account = contaCorrente; }
    public String getIdSindicato() { return idSyndicate; }
    public void setIdSindicato(String idSindicato) { this.idSyndicate = idSindicato; }
    public double getTaxaSindical() { return syndicateFee; }
    public void setTaxaSindical(double taxaSindical) { this.syndicateFee = taxaSindical; }
    public double getDebtSyndicate() { return debtSyndicate; }
    public void setDebtSyndicate(double debtSyndicate) { this.debtSyndicate = debtSyndicate; }
    public List<TaxaServico> getTaxasServicos() { return serviceCharges; }
    public void setTaxasServicos(List<TaxaServico> taxasServicos) { this.serviceCharges = taxasServicos; }
    public String getAgendaPagamento() {
    return agendaPagamento;
}
    public void setAgendaPagamento(String agendaPagamento) { this.agendaPagamento = agendaPagamento; }
}
