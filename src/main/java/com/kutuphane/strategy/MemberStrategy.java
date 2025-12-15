package com.kutuphane.strategy;

import javafx.scene.control.Button;

public class MemberStrategy implements IAuthorizationStrategy {
    @Override
    public void apply(Button btnUsers, Button btnBooks, Button btnLoans, Button btnReports, Button btnProfile) {
        // Üye sadece Kitaplar (inceleme), Ödünç ve Profil'e erişebilir.
        btnUsers.setDisable(true);      // Üye yönetimi kapalı
        btnBooks.setDisable(false);     // Kitapları görebilir
        btnLoans.setDisable(false);     // Ödünç/İade yapabilir
        btnReports.setDisable(true);    // Raporlar kapalı
        btnProfile.setDisable(false);   // Profil açık
    }
}