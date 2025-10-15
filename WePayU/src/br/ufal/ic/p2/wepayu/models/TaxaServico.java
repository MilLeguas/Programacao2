package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaxaServico {
    private String chargeDate;
    private double amount;

    public TaxaServico(LocalDate data, double valor) {
        this.chargeDate = data.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.amount = valor;
    }
    public TaxaServico() {}

    public LocalDate getDataAsLocalDate() {return (this.chargeDate == null || this.chargeDate.isEmpty()) ? null : LocalDate.parse(this.chargeDate);}
    public String getData() { return chargeDate; }
    public void setData(String data) { this.chargeDate = data; }
    public double getValor() { return amount; }
    public void setValor(double valor) { this.amount = valor; }
}