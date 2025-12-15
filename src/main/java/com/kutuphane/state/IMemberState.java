package com.kutuphane.state;

import com.kutuphane.model.Member;

/**
 * Üyenin durumuna bağlı olarak alabileceği aksiyonları tanımlar.
 */
public interface IMemberState {

    /**
     * Üyenin ödünç alıp alamayacağını kontrol eder.
     */
    boolean canLoan(Member member);

    /**
     * Durum geçişini tanımlar (Örneğin, borç ödendiğinde ne olmalı).
     */
    void handleFinePayment(Member member);

    /**
     * Durumun açıklamasını verir.
     */
    String getStatusDescription();
}