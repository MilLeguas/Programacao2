package br.ufal.ic.p2.wepayu.exceptions;

public class PaymentMethodInvalidException extends Exception {
    public PaymentMethodInvalidException() {
        super("Metodo de pagamento invalido.");
    }
}
