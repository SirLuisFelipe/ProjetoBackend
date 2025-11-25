package com.projeto.demo.repositories.projections;

public interface PaymentCountProjection {
    Long getPaymentId();
    String getPaymentName();
    Long getTotal();
}
