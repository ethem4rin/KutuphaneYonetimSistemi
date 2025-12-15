package com.kutuphane.state;

import com.kutuphane.model.Member;

public class ActiveState implements IMemberState {
    @Override
    public boolean canLoan(Member member) {
        // Ek kurallar (maksimum ödünç sayısı) buraya eklenebilir.
        return true;
    }

    @Override
    public void handleFinePayment(Member member) {
        // Aktif durumda ödeme yapılması gerekmez (zaten borçsuz varsayılır)
        System.out.println(member.getFullName() + ": Zaten aktif durumda.");
    }

    @Override
    public String getStatusDescription() {
        return "Aktif (Ödünç alabilir)";
    }
}