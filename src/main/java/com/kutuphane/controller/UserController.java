package com.kutuphane.controller;

import com.kutuphane.model.Librarian;
import com.kutuphane.model.Member;
import com.kutuphane.model.User;
import com.kutuphane.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class UserController {

    private final UserRepository userRepository = new UserRepository();
    private ObservableList<User> userList;

    // FXML Alanları (Tablo ve Sütunlar)
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Double> debtColumn;

    // FXML Alanları (Formlar)
    @FXML private TextField txtUsername;
    @FXML private TextField txtFullName;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;

    @FXML
    public void initialize() {
        // ComboBox'ı Rollerle doldur
        cmbRole.setItems(FXCollections.observableArrayList("UYE", "PERSONEL"));

        // Tablo sütunlarını Model alanlarına bağla
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        // NOT: debtColumn için özel bir cell value factory gerekebilir, şimdilik atlıyoruz.

        // Tabloya tıklama olayı
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        fillForm(newSelection);
                    }
                });

        loadUsers();
    }

    private void loadUsers() {
        userList = FXCollections.observableArrayList(userRepository.findAll());
        userTable.setItems(userList);
    }

    private void fillForm(User user) {
        txtUsername.setText(user.getUsername());
        txtFullName.setText(user.getFullName());
        // Şifre alanını güvenlik nedeniyle boş bırakıyoruz
        txtPassword.clear();
        cmbRole.setValue(user.getRole());
    }

    // =========================================================================
    // CRUD METOTLARI (ÇALIŞAN MANTIK)
    // =========================================================================

    @FXML
    private void handleAddUser() {
        if (!validateFields()) return;

        String role = cmbRole.getValue();
        // NOT: Yeni kullanıcılar için sadece temel User nesnesi oluşturuyoruz
        User newUser;
        if ("UYE".equals(role)) {
            // Yeni üye (Borç 0.0)
            newUser = new Member(0, txtUsername.getText(), txtPassword.getText(), txtFullName.getText(), 0.0);
        } else {
            // Yeni personel
            newUser = new Librarian(0, txtUsername.getText(), txtPassword.getText(), txtFullName.getText());
        }

        User savedUser = userRepository.save(newUser);

        if (savedUser != null) {
            userList.add(savedUser);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", savedUser.getFullName() + " başarıyla eklendi.");
            handleClearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı ekleme başarısız oldu. Kullanıcı adı mevcut olabilir.");
        }
    }

    @FXML
    private void handleUpdateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null || !validateFields()) return;

        // Şifre alanı boşsa, eski şifreyi koru
        String newPassword = txtPassword.getText().isEmpty() ? selectedUser.getPassword() : txtPassword.getText();
        String role = cmbRole.getValue();

        // Güncellenecek nesneyi oluştur
        User updatedUser;
        if ("UYE".equals(role)) {
            // Member veya Librarian fark etmeksizin yeni role göre nesne oluşturulur.
            updatedUser = new Member(selectedUser.getId(), txtUsername.getText(), newPassword, txtFullName.getText(), 0.0); // Borç güncellenmedi
        } else {
            updatedUser = new Librarian(selectedUser.getId(), txtUsername.getText(), newPassword, txtFullName.getText());
        }

        User result = userRepository.update(updatedUser);

        if (result != null) {
            // Listeyi güncelleme
            userList.set(userList.indexOf(selectedUser), result);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", result.getFullName() + " başarıyla güncellendi.");
            handleClearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı güncelleme başarısız oldu. Konsolu kontrol edin.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Lütfen silmek için tablodan bir kullanıcı seçin.");
            return;
        }

        Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Onay Gerekli", selectedUser.getFullName() + " silinecektir. Emin misiniz?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            userRepository.delete(selectedUser.getId());
            userList.remove(selectedUser);
            showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kullanıcı başarıyla silindi!");
            handleClearForm();
        }
    }

    @FXML
    private void handleClearForm() {
        txtUsername.clear();
        txtFullName.clear();
        txtPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        userTable.getSelectionModel().clearSelection();
    }

    // =========================================================================
    // YARDIMCI METOTLAR
    // =========================================================================

    private boolean validateFields() {
        if (txtUsername.getText().isEmpty() || txtFullName.getText().isEmpty() || cmbRole.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Kullanıcı Adı, Ad Soyad ve Rol alanları boş bırakılamaz.");
            return false;
        }
        // Ekleme yapılıyorsa (yani tablodan seçim yapılmamışsa) şifre de zorunlu
        if (userTable.getSelectionModel().getSelectedItem() == null && txtPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Yeni kullanıcı eklerken şifre alanı boş bırakılamaz.");
            return false;
        }
        return true;
    }

    private Optional<ButtonType> showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}