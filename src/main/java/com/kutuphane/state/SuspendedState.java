package com.kutuphane.state;

import com.kutuphane.model.Member;

public class SuspendedState implements IMemberState {
    @Override
    public boolean canLoan(Member member) {
        return false;
    }

    @Override
    public void handleFinePayment(Member member) {
        // Askıdaki üye borç öderse (varsa) ve sorun çözülürse Aktif duruma geçmeli
        if (member.getDebtAmount() <= 0) {
            member.setState(new ActiveState());
            System.out.println(member.getFullName() + ": Askıdan çıkarıldı. Artık Aktif.");
        } else {
            System.out.println(member.getFullName() + ": Askıdayken borç ödemesi yapılmalı.");
        }
    }

    @Override
    public String getStatusDescription() {
        return "Askıda (Ödünç alamaz)";
    }
}