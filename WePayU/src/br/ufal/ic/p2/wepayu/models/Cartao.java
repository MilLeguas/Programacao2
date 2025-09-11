package br.ufal.ic.p2.wepayu.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Cartao {
    private String dateStr;
    private double hoursWorked;

    public Cartao(LocalDate data, double horasTrabalhadas) {
        this.dateStr = data.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.hoursWorked = horasTrabalhadas;
    }
    public Cartao() {}

    public LocalDate getDataAsLocalDate() {return (this.dateStr == null || this.dateStr.isEmpty()) ? null : LocalDate.parse(this.dateStr);}
    public String getData() { return dateStr; }
    public void setData(String data) { this.dateStr = data; }
    public double getHorasTrabalhadas() { return hoursWorked; }
    public void setHorasTrabalhadas(double horasTrabalhadas) { this.hoursWorked = horasTrabalhadas; }
}