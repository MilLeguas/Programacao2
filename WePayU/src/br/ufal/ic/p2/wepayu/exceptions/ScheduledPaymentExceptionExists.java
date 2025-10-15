package br.ufal.ic.p2.wepayu.exceptions;

public class ScheduledPaymentExceptionExists extends RuntimeException {
    public ScheduledPaymentExceptionExists( ) {
        super("Agenda de pagamentos ja existe");
    }
}
