package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.EmployeeNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Comissionado extends Empregado {
    private double commissionRate;
    private double vendasRealizadas;
    private List<Venda> salesRecords = new ArrayList<>();

    public Comissionado(String id, String nome, String endereco, String tipo, double salario, double comissao) throws EmployeeNotFoundException {
        super(id, nome, endereco, tipo, salario);
        this.commissionRate = comissao;
    }
    public Comissionado() {}
    public Comissionado(Comissionado other) {
        super(other);
        this.commissionRate = other.commissionRate;
        this.salesRecords = new ArrayList<>(other.salesRecords);
    }

    public double getVendasNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {return salesRecords.stream().filter(sale -> !sale.getDataAsLocalDate().isBefore(dataInicial) && !sale.getDataAsLocalDate().isAfter(dataFinal)).mapToDouble(Venda::getValor).sum();}
    public void lancaVenda(Venda venda) { salesRecords.add(venda); }
    public void removerVenda(Venda venda) { salesRecords.remove(venda); }
    public double getComissao() { return commissionRate; }
    public void setComissao(double comissao) { this.commissionRate = comissao; }
    public List<Venda> getVendas() { return salesRecords; }
    public void setVendas(List<Venda> vendas) { this.salesRecords = vendas; }
    public double getVendasRealizadas() { return vendasRealizadas; }
    public void setVendasRealizadas(double vendasRealizadas) { this.vendasRealizadas = vendasRealizadas; }
}