package br.ufal.ic.p2.wepayu.models;

import br.ufal.ic.p2.wepayu.exceptions.EmployeeNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Horista extends Empregado {
    private double horasNormaisTrabalhadas;
    private double horasExtrasTrabalhadas;
    private List<Cartao> timeCards = new ArrayList<>();

    public Horista(String id, String nome, String endereco, String tipo, double salario) throws EmployeeNotFoundException {super(id, nome, endereco, tipo, salario);}
    public Horista() {}
    public Horista(Horista other) {
        super(other);
        this.timeCards = new ArrayList<>(other.timeCards);
    }

    public double[] getHorasNoPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        double[] totalHours = new double[2];
        this.timeCards.stream().filter(card -> !card.getDataAsLocalDate().isBefore(dataInicial) && !card.getDataAsLocalDate().isAfter(dataFinal)).forEach(card -> {
            double worked = card.getHorasTrabalhadas();
            totalHours[0] += Math.min(worked, 8);
            totalHours[1] += Math.max(0, worked - 8);
        });
        return totalHours;
    }

    public void lancaCartao(Cartao cartao) { timeCards.add(cartao); }
    public void removerCartao(Cartao cartao) { timeCards.remove(cartao); }
    public List<Cartao> getCartoesDePonto() { return timeCards; }
    public void setCartoesDePonto(List<Cartao> cartoesDePonto) { this.timeCards = cartoesDePonto; }
    public double getHorasNormaisTrabalhadas() { return horasNormaisTrabalhadas; }
    public void setHorasNormaisTrabalhadas(double horasNormais) { this.horasNormaisTrabalhadas = horasNormais; }
    public double getHorasExtrasTrabalhadas() { return horasExtrasTrabalhadas; }
    public void setHorasExtrasTrabalhadas(double horasExtras) { this.horasExtrasTrabalhadas = horasExtras; }
}