package com.kutuphane.strategy;

import javafx.scene.control.Button;

/**
 * Farklı kullanıcı rollerine göre yetkilendirme (buton durumlarını ayarlama) stratejisi arayüzü.
 */
public interface IAuthorizationStrategy {

    /**
     * Verilen butonların görünürlüğünü/etkinliğini role göre ayarlar.
     */
    void apply(Button btnUsers, Button btnBooks, Button btnLoans, Button btnReports, Button btnProfile);
}