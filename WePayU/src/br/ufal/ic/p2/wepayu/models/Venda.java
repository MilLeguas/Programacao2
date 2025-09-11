package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Venda {
    private String dateRecord;
    private double amount;

    public Venda(LocalDate data, double valor) {
        this.dateRecord = data.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.amount = valor;
    }
    public Venda() {}

    public LocalDate getDataAsLocalDate() {return (this.dateRecord == null || this.dateRecord.isEmpty()) ? null : LocalDate.parse(this.dateRecord);}
    public String getData() { return dateRecord; }
    public void setData(String data) { this.dateRecord = data; }
    public double getValor() { return amount; }
    public void setValor(double valor) { this.amount = valor; }
}