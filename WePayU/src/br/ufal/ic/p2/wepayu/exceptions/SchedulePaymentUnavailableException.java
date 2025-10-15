package br.ufal.ic.p2.wepayu.exceptions;

public class SchedulePaymentUnavailableException extends RuntimeException {
    public SchedulePaymentUnavailableException() {super("Agenda de pagamento nao esta disponivel");}
}
