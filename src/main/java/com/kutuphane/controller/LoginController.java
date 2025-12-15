package com.kutuphane.controller;

import com.kutuphane.MainApplication;
import com.kutuphane.model.User;
import com.kutuphane.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    private final UserRepository userRepository = new UserRepository();

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        User user = userRepository.authenticate(username, password);

        if (user != null) {
            showAlert("Başarılı", "Giriş başarılı! Hoş geldiniz, " + user.getFullName(), Alert.AlertType.INFORMATION);
            openMainView(user);
        } else {
            showAlert("Hata", "Kullanıcı adı veya şifre hatalı.", Alert.AlertType.ERROR);
        }
    }

    private void openMainView(User user) {
        try {
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            MainController mainController = loader.getController();
            mainController.setActiveUser(user); // Kullanıcı bilgisini ve rolünü MainController'a aktar

            currentStage.setScene(scene);
            currentStage.setTitle("Kütüphane Yönetim Sistemi - " + user.getRole());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Sistem Hatası", "Ana ekran yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}