package com.kutuphane.controller;

import com.kutuphane.model.Book;
import com.kutuphane.repository.BookRepository;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;

public class BookController {

    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtPublisher;
    @FXML private TextField txtYear;
    @FXML private TextField txtISBN;
    @FXML private TextField txtQuantity;
    @FXML private TextField txtLocation;

    @FXML private FlowPane bookCardFlowPane;
    @FXML private Label formTitle;

    private final BookRepository bookRepository = new BookRepository();
    private Book selectedBook = null;

    @FXML
    public void initialize() {
        loadBookCards();
        handleClearForm();
    }

    private void loadBookCards() {
        List<Book> books = bookRepository.findAll();
        bookCardFlowPane.getChildren().clear();

        for (Book book : books) {
            VBox card = createBookCard(book);
            bookCardFlowPane.getChildren().add(card);
        }
    }

    private VBox createBookCard(Book book) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_LEFT);
        card.getStyleClass().add("book-card");
        card.setMinWidth(200);
        card.setMinHeight(140);

        Label title = new Label(book.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #003366;");
        Label author = new Label("Yazar: " + book.getAuthor());

        int loanCount = bookRepository.getLoanCount(book.getId());
        int availableCount = book.getQuantity() - loanCount;

        Label available = new Label("Mevcut: " + availableCount + " / " + book.getQuantity());
        available.setStyle(availableCount > 0 ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        card.setOnMouseClicked(e -> selectBookForForm(book));

        card.getChildren().addAll(title, author, available);
        return card;
    }

    private void selectBookForForm(Book book) {
        selectedBook = book;
        formTitle.setText("Kitap Düzenle (ID: " + book.getId() + ")");

        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtISBN.setText(book.getIsbn());
        txtPublisher.setText(book.getPublisher());
        txtYear.setText(String.valueOf(book.getYear()));
        txtQuantity.setText(String.valueOf(book.getQuantity()));
        txtLocation.setText(book.getLocation());
    }

    @FXML
    private void handleSaveBook() {
        if (!validateInput()) {
            showAlert("Hata", "Lütfen tüm zorunlu alanları (Başlık, Yazar, ISBN, Adet) doğru formatta doldurun.", AlertType.ERROR);
            return;
        }

        Book book = createBookFromForm();

        if (selectedBook == null) {
            bookRepository.save(book);
            showAlert("Başarılı", "Kitap başarıyla eklendi.", AlertType.INFORMATION);
        } else {
            book.setId(selectedBook.getId());
            bookRepository.update(book);
            showAlert("Başarılı", "Kitap başarıyla güncellendi.", AlertType.INFORMATION);
        }

        handleClearForm();
        loadBookCards();
    }

    @FXML
    private void handleDeleteBook() {
        if (selectedBook == null) {
            showAlert("Hata", "Lütfen silmek için bir kart seçin.", AlertType.WARNING);
            return;
        }

        bookRepository.delete(selectedBook.getId());
        showAlert("Başarılı", "Kitap başarıyla silindi.", AlertType.INFORMATION);
        handleClearForm();
        loadBookCards();
    }

    @FXML
    private void handleClearForm() {
        selectedBook = null;
        formTitle.setText("Yeni Kitap Kaydı / Güncelleme");

        txtTitle.clear();
        txtAuthor.clear();
        txtISBN.clear();
        txtPublisher.clear();
        txtYear.clear();
        txtQuantity.clear();
        txtLocation.clear();
    }

    private Book createBookFromForm() {
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String publisher = txtPublisher.getText();
        String yearStr = txtYear.getText();
        String isbn = txtISBN.getText();
        String quantityStr = txtQuantity.getText();
        String location = txtLocation.getText();

        try {
            int year = yearStr.isEmpty() ? 0 : Integer.parseInt(yearStr);
            int quantity = Integer.parseInt(quantityStr);

            return new Book(0, title, author, isbn, publisher, year, quantity, location);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean validateInput() {
        if (txtTitle.getText().isEmpty() || txtAuthor.getText().isEmpty() ||
                txtISBN.getText().isEmpty() || txtQuantity.getText().isEmpty()) {
            return false;
        }

        try {
            if (!txtYear.getText().isEmpty()) {
                Integer.parseInt(txtYear.getText());
            }
            if (Integer.parseInt(txtQuantity.getText()) <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}