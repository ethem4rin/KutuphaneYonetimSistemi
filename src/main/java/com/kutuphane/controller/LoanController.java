package com.kutuphane.controller;

import com.kutuphane.model.Loan;
import com.kutuphane.model.Book;
import com.kutuphane.model.User;
import com.kutuphane.model.Member; // State kontrolü için Member modeli eklendi
import com.kutuphane.repository.LoanRepository;
import com.kutuphane.repository.BookRepository;
import com.kutuphane.repository.UserRepository;
import com.kutuphane.observer.DataObserver;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

// DataObserver arayüzünü uygulayarak Repository'deki değişiklikleri dinler
public class LoanController implements DataObserver {

    @FXML private TableView<Loan> loanTable;
    @FXML private TableColumn<Loan, Integer> loanIdColumn;
    @FXML private TableColumn<Loan, Integer> bookIdColumn;
    @FXML private TableColumn<Loan, Integer> userIdColumn;
    @FXML private TableColumn<Loan, String> loanDateColumn;
    @FXML private TableColumn<Loan, String> dueDateColumn;
    @FXML private TableColumn<Loan, String> returnDateColumn;

    // FXML'den bağlanan form elemanları
    @FXML private ComboBox<User> cmbUser;
    @FXML private ComboBox<Book> cmbBook;
    @FXML private DatePicker datePickerDueDate;

    private final LoanRepository loanRepository = new LoanRepository();
    private final BookRepository bookRepository = new BookRepository();
    private final UserRepository userRepository = new UserRepository();


    @FXML
    public void initialize() {
        // 1. TableView Başlatma
        if (loanIdColumn != null) loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (bookIdColumn != null) bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        if (userIdColumn != null) userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        if (loanDateColumn != null) loanDateColumn.setCellValueFactory(new PropertyValueFactory<>("loanDate"));
        if (dueDateColumn != null) dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        if (returnDateColumn != null) returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));

        // 2. Observer olarak abone ol
        LoanRepository.getLoanObservable().addObserver(this);

        // 3. Veri Yükleme
        loadComboBoxData();
        loadLoans();
    }

    /**
     * ComboBox'ları gerekli verilerle doldurur (Kullanıcı ve Kitaplar)
     */
    private void loadComboBoxData() {
        // Not: Sadece 'UYE' rolündeki kullanıcılar filtre edilebilir
        List<User> allUsers = userRepository.findAll();
        cmbUser.setItems(FXCollections.observableArrayList(allUsers));

        List<Book> allBooks = bookRepository.findAll();
        cmbBook.setItems(FXCollections.observableArrayList(allBooks));

        // ComboBox'ta sadece isim gösterilmesi için özel hücre sınıfını kullanır
        cmbUser.setCellFactory(lv -> new UserListCell());
        cmbUser.setButtonCell(new UserListCell());

        cmbBook.setCellFactory(lv -> new BookListCell());
        cmbBook.setButtonCell(new BookListCell());
    }

    private void loadLoans() {
        List<Loan> activeLoans = loanRepository.findAllActiveLoans();
        loanTable.setItems(FXCollections.observableArrayList(activeLoans));
    }

    // KRİTİK: Repository'den gelen güncelleme sinyali
    @Override
    public void updateData() {
        // Veritabanı işlemi başarıyla tamamlandığında otomatik çağrılır
        loadLoans();
        loadComboBoxData(); // Kitap stoğu değişmiş olabileceği için ComboBox'ları da yenileyelim
    }

    @FXML
    private void handleLoanBook() {
        User selectedUser = cmbUser.getSelectionModel().getSelectedItem();
        Book selectedBook = cmbBook.getSelectionModel().getSelectedItem();
        LocalDate dueDate = datePickerDueDate.getValue();

        if (selectedUser == null || selectedBook == null || dueDate == null) {
            showAlert("Uyarı", "Lütfen bir üye, bir kitap ve iade tarihi seçin.", AlertType.WARNING);
            return;
        }

        if (dueDate.isBefore(LocalDate.now())) {
            showAlert("Uyarı", "İade tarihi bugünden önce olamaz.", AlertType.WARNING);
            return;
        }

        // ====== STATE DESENİ KONTROLÜ ======
        if (selectedUser instanceof Member) {
            Member member = (Member) selectedUser;

            if (!member.canLoan()) {
                showAlert("İşlem Reddedildi",
                        "Seçili üye durumu (" + member.getState().getStatusDescription() + ") nedeniyle ödünç alamaz.",
                        AlertType.ERROR);
                return;
            }
        }
        // ===================================

        // KRİTİK STOK KONTROLÜ
        // Basitlik için sadece tek bir stok kontrolü varsayımı yapılıyor.

        String loanDateStr = LocalDate.now().toString();
        String dueDateStr = dueDate.toString();

        Loan newLoan = new Loan(0, selectedBook.getId(), selectedUser.getId(), loanDateStr, dueDateStr, null);

        if (loanRepository.save(newLoan) != null) {
            showAlert("Başarılı", "Ödünç kaydı yapıldı: " + selectedBook.getTitle(), AlertType.INFORMATION);
            // Observer yapısı sayesinde tablo ve ComboBox'lar otomatik güncellenir.
        } else {
            showAlert("Hata", "Ödünç kaydı yapılırken bir sorun oluştu. Stok veya DB hatası olabilir.", AlertType.ERROR);
        }
    }

    @FXML
    private void handleReturnBook() {
        Loan selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            showAlert("Uyarı", "Lütfen iade edilecek bir ödünç kaydı seçin.", AlertType.WARNING);
            return;
        }

        if (loanRepository.completeLoan(selectedLoan.getId())) {
            showAlert("Başarılı", "Kitap başarıyla iade alındı.", AlertType.INFORMATION);
            // Observer yapısı sayesinde tablo ve ComboBox'lar otomatik güncellenir.
        } else {
            showAlert("Hata", "İade işlemi tamamlanamadı.", AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ComboBox'ta Nesne Adlarını Göstermek İçin Yardımcı Sınıflar (ListCell)

    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            // Not: User modelinin getFullName ve getRole metotları kullanılmalı
            setText(empty || user == null ? null : user.getFullName() + " (" + user.getRole() + ")");
        }
    }

    private static class BookListCell extends ListCell<Book> {
        @Override
        protected void updateItem(Book book, boolean empty) {
            super.updateItem(book, empty);
            // Not: Book modelinin getTitle ve getAuthor metotları kullanılmalı
            setText(empty || book == null ? null : book.getTitle() + " - " + book.getAuthor());
        }
    }
}