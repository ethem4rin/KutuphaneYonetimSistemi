package com.kutuphane.model;

import com.kutuphane.state.ActiveState;
import com.kutuphane.state.IMemberState;

// User modelinden türetilmiştir
public class Member extends User {

    private double debtAmount;
    private IMemberState currentState; // KRİTİK: State objesini tutar

    public Member(int id, String username, String password, String fullName, double debt) {
        super(id, username, password, fullName, "UYE");
        this.debtAmount = debt;
        // Başlangıç durumu
        this.currentState = new ActiveState();

        // Eğer borç varsa veya başka bir kısıtlama varsa durumu güncelleyebiliriz.
        // Örneğin: if (debt > MAX_DEBT) setState(new SuspendedState());
    }

    // STATE Deseni Metotları: Davranışı Durum objesine delege et
    public boolean canLoan() {
        return currentState.canLoan(this);
    }

    public void handleFinePayment() {
        currentState.handleFinePayment(this);
    }

    public void setState(IMemberState newState) {
        this.currentState = newState;
    }

    public IMemberState getState() {
        return currentState;
    }

    // Getterlar ve Setterlar
    public double getDebtAmount() { return debtAmount; }
    public void setDebtAmount(double debtAmount) { this.debtAmount = debtAmount; }
}