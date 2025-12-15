package com.kutuphane.controller;

import com.kutuphane.model.User;
import com.kutuphane.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class ProfileController {

    @FXML private Label lblUsername;
    @FXML private TextField txtFullName;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;

    private final UserRepository userRepository = new UserRepository();
    private User currentUser;

    public void initializeProfile(User user) {
        this.currentUser = user;
        lblUsername.setText("Kullanıcı Adı: " + user.getUsername() + " (" + user.getRole() + ")");
        txtFullName.setText(user.getFullName());
    }

    @FXML
    private void handleSaveProfile() {
        if (currentUser == null) return;

        String newName = txtFullName.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (!newPass.isEmpty() && !newPass.equals(confirmPass)) {
            showAlert("Hata", "Yeni şifreler eşleşmiyor.", Alert.AlertType.ERROR);
            return;
        }

        // Ad Soyad güncellemesi
        if (!newName.equals(currentUser.getFullName())) {
            currentUser.setFullName(newName);
        }

        // Şifre güncellemesi
        if (!newPass.isEmpty()) {
            currentUser.setPassword(newPass);
        }

        User result = userRepository.update(currentUser);

        if (result != null) {
            showAlert("Başarılı", "Profiliniz başarıyla güncellendi.", Alert.AlertType.INFORMATION);
            // Formu temizle
            txtNewPassword.clear();
            txtConfirmPassword.clear();

            // NOT: Ana menüdeki lblUserInfo'yu güncellemek için ek bir işlem gerekir.
        } else {
            showAlert("Hata", "Güncelleme başarısız oldu. Konsolu kontrol edin.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}