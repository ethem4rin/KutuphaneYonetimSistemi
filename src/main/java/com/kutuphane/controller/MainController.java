package com.kutuphane.controller;

import com.kutuphane.MainApplication;
import com.kutuphane.model.User;
import com.kutuphane.strategy.IAuthorizationStrategy; // Strategy Interface
import com.kutuphane.strategy.AdminPersonnelStrategy; // Admin Strategy
import com.kutuphane.strategy.MemberStrategy; // Member Strategy

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML private AnchorPane contentArea;
    @FXML private Label lblUserInfo;

    @FXML private Button btnUsers;
    @FXML private Button btnBooks;
    @FXML private Button btnLoans;
    @FXML private Button btnReports;
    @FXML private Button btnProfile;

    private User activeUser = null;

    @FXML
    public void initialize() {
        handleWelcome();
        // Uygulama ilk açıldığında yetkilendirme yok
        applyAuthorization(null);
    }

    public void setActiveUser(User user) {
        this.activeUser = user;
        if (user != null) {
            lblUserInfo.setText("Giriş Yapan: " + user.getFullName() + " (" + user.getRole() + ")");
            applyAuthorization(user.getRole());
        } else {
            lblUserInfo.setText("Giriş Yapılmadı");
            applyAuthorization(null);
        }
    }

    // KRİTİK DÜZELTME: Strategy kullanarak yetkilendirmeyi soyutla
    private void applyAuthorization(String role) {
        IAuthorizationStrategy strategy;

        if ("ADMIN".equals(role) || "PERSONEL".equals(role)) {
            strategy = new AdminPersonnelStrategy();
        } else if ("UYE".equals(role)) {
            strategy = new MemberStrategy();
        } else {
            // Çıkış yapıldığında veya oturum açılmadığında (Her şeyi kapatan anonim strateji)
            strategy = (btnUsers, btnBooks, btnLoans, btnReports, btnProfile) -> {
                btnUsers.setDisable(true);
                btnBooks.setDisable(true);
                btnLoans.setDisable(true);
                btnReports.setDisable(true);
                btnProfile.setDisable(true);
            };
        }

        // Seçilen stratejiyi uygula
        strategy.apply(btnUsers, btnBooks, btnLoans, btnReports, btnProfile);
    }


    @FXML private void handleWelcome() {
        contentArea.getChildren().clear();
        Label welcome = new Label("Kütüphane Yönetim Sistemine Hoş Geldiniz!");
        welcome.setStyle("-fx-font-size: 24pt; -fx-padding: 50;");
        contentArea.getChildren().add(welcome);
    }

    @FXML private void handleBookManagement() {
        loadView("book-management-view.fxml");
    }

    @FXML private void handleUserManagement() {
        loadView("user-management-view.fxml");
    }

    @FXML private void handleLoanManagement() {
        loadView("loan-management-view.fxml");
    }

    @FXML private void handleReports() {
        loadView("reports-view.fxml");
    }

    @FXML private void handleProfileEdit() {
        loadView("profile-edit-view.fxml");
    }


    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(fxmlFile));
            AnchorPane pane = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(pane);

            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new Label("Hata: " + fxmlFile + " yüklenemedi. FXML adını kontrol edin."));
        }
    }

    @FXML private void handleLogout() {
        try {
            Stage currentStage = (Stage) contentArea.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            currentStage.setScene(scene);
            currentStage.setTitle("Kütüphane Yönetim Sistemi Giriş");
            this.activeUser = null;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Çıkış yaparken giriş ekranı yüklenemedi.");
        }
    }

    @FXML private void handleExit() {
        Platform.exit();
    }
}