package com.kutuphane.repository;

import com.kutuphane.model.Book;
import com.kutuphane.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookRepository implements IBaseRepository<Book> {

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getInstance().getConnection();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, isbn, publisher, publication_year, total_quantity, location FROM Books ORDER BY title ASC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("isbn"),
                        rs.getString("publisher"),
                        rs.getInt("publication_year"),
                        rs.getInt("total_quantity"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Kitapları çekerken hata oluştu: " + e.getMessage());
        }
        return books;
    }

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO Books (title, author, isbn, publisher, publication_year, total_quantity, available_quantity, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getYear());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setInt(7, book.getQuantity());
            pstmt.setString(8, book.getLocation());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Kitap kaydederken hata oluştu: " + e.getMessage());
            return null;
        }
        return book;
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, isbn = ?, publisher = ?, publication_year = ?, total_quantity = ?, location = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setString(4, book.getPublisher());
            pstmt.setInt(5, book.getYear());
            pstmt.setInt(6, book.getQuantity());
            pstmt.setString(7, book.getLocation());
            pstmt.setInt(8, book.getId());

            pstmt.executeUpdate();
            return book;
        } catch (SQLException e) {
            System.err.println("Kitap güncellerken hata oluştu: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Books WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kitap silinirken hata oluştu: " + e.getMessage());
        }
    }

    @Override
    public Book findById(int id) {
        // Bu metot projenizde kullanılmadığı için basitçe null dönebilir.
        return null;
    }

    public int getLoanCount(int bookId) {
        String sql = "SELECT COUNT(id) AS loan_count FROM Loans WHERE book_id = ? AND return_date IS NULL";
        int loanCount = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loanCount = rs.getInt("loan_count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ödünç sayısını çekerken hata oluştu: " + e.getMessage());
        }
        return loanCount;
    }
}