package com.kutuphane.repository;

import com.kutuphane.model.Loan;
import com.kutuphane.db.DatabaseManager;
import com.kutuphane.observer.ObservableData; // Observer entegrasyonu için eklendi
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository implements IBaseRepository<Loan> {

    // Observer Deseni için Subject/Observable nesnesi
    private static final ObservableData loanObservable = new ObservableData();

    public static ObservableData getLoanObservable() {
        return loanObservable;
    }

    private Connection getConnection() throws SQLException {
        // Her çağrıda yeni, açık bir bağlantı döndürür.
        return DatabaseManager.getInstance().getConnection();
    }

    // ------------------- FIND METOTLARI -------------------

    @Override
    public List<Loan> findAll() {
        return findAllActiveLoans();
    }

    public List<Loan> findAllActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loans WHERE return_date IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getInt("user_id"),
                        rs.getString("loan_date"),
                        rs.getString("due_date"),
                        rs.getString("return_date") // Null olacaktır
                ));
            }
        } catch (SQLException e) {
            System.err.println("Aktif ödünçleri çekerken hata oluştu: " + e.getMessage());
        }
        return loans;
    }

    @Override
    public Loan findById(int id) {
        String sql = "SELECT * FROM Loans WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Loan(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getInt("user_id"),
                            rs.getString("loan_date"),
                            rs.getString("due_date"),
                            rs.getString("return_date")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Ödünç kaydı bulunurken hata oluştu: " + e.getMessage());
        }
        return null;
    }


    // ------------------- CRUD İŞLEMLERİ -------------------

    @Override
    public Loan save(Loan loan) {
        String sql = "INSERT INTO Loans (book_id, user_id, loan_date, due_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Transaction başlat

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, loan.getBookId());
                pstmt.setInt(2, loan.getUserId());
                pstmt.setString(3, loan.getLoanDate());
                pstmt.setString(4, loan.getDueDate());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            loan.setId(generatedKeys.getInt(1));

                            // Kitap Envanterini Güncelle (Available Quantity azaltma)
                            updateBookQuantity(loan.getBookId(), conn, -1);

                            conn.commit(); // Başarılı, işlemi tamamla
                            loanObservable.notifyObservers(); // KRİTİK: Observer'lara haber ver
                            return loan;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Ödünç kaydederken hata oluştu: " + e.getMessage());
            // conn.rollback() gerekebilir
        }
        return null;
    }

    /**
     * İADE İŞLEMİNİ TAMAMLAR (LoanController tarafından çağrılır)
     */
    public boolean completeLoan(int loanId) {
        String updateLoanSql = "UPDATE Loans SET return_date = ?, fine_amount = 0.0 WHERE id = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            // 1. İade tarihini güncelle
            try (PreparedStatement pstmt = conn.prepareStatement(updateLoanSql)) {
                pstmt.setString(1, LocalDate.now().toString());
                pstmt.setInt(2, loanId);
                pstmt.executeUpdate();
            }

            // 2. Kitap ID'sini bul
            int bookId = getBookIdByLoanId(loanId, conn);
            if (bookId > 0) {
                // 3. Envanteri artır (change = +1)
                updateBookQuantity(bookId, conn, 1);
            }

            conn.commit();
            loanObservable.notifyObservers(); // KRİTİK: Observer'lara haber ver
            return true;
        } catch (SQLException e) {
            System.err.println("İade işlemini tamamlarken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Loan update(Loan loan) {
        String sql = "UPDATE Loans SET book_id = ?, user_id = ?, loan_date = ?, due_date = ?, return_date = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getUserId());
            pstmt.setString(3, loan.getLoanDate());
            pstmt.setString(4, loan.getDueDate());
            pstmt.setString(5, loan.getReturnDate());
            pstmt.setInt(6, loan.getId());

            pstmt.executeUpdate();
            return loan;
        } catch (SQLException e) {
            System.err.println("Ödünç kaydı güncellenirken hata oluştu: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Loans WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ödünç kaydı silinirken hata oluştu: " + e.getMessage());
        }
    }

    // ------------------- YARDIMCI METOTLAR -------------------

    private void updateBookQuantity(int bookId, Connection conn, int change) throws SQLException {
        // Kitapların mevcut miktarını günceller.
        String sql = "UPDATE Books SET available_quantity = available_quantity + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, change);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        }
    }

    private int getBookIdByLoanId(int loanId, Connection conn) throws SQLException {
        String sql = "SELECT book_id FROM Loans WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("book_id");
            }
        }
        return -1;
    }
}