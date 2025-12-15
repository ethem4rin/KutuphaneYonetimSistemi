package com.kutuphane.strategy;

import javafx.scene.control.Button;

public class AdminPersonnelStrategy implements IAuthorizationStrategy {
    @Override
    public void apply(Button btnUsers, Button btnBooks, Button btnLoans, Button btnReports, Button btnProfile) {
        // Personel/Admin her şeye erişebilir
        btnUsers.setDisable(false);
        btnBooks.setDisable(false);
        btnLoans.setDisable(false);
        btnReports.setDisable(false);
        btnProfile.setDisable(false);
    }
}