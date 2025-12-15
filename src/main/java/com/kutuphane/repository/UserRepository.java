package com.kutuphane.repository;

import com.kutuphane.db.DatabaseManager;
import com.kutuphane.model.Librarian;
import com.kutuphane.model.Member;
import com.kutuphane.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// KRİTİK DEĞİŞİKLİK: IBaseRepository yerine BaseRepository'den türetildi
public class UserRepository extends BaseRepository<User> {

    // Abstract metodu implemente ediyoruz: Veritabanı bağlantısını açar.
    @Override
    protected Connection getOpenConnection() throws SQLException {
        // DatabaseManager, her çağrıda yeni bir bağlantı döndürme mantığını içerir.
        return DatabaseManager.getInstance().getConnection();
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        // password_hash sütunu veritabanı şemasından çekilir
        String password = rs.getString("password_hash");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        double debt = 0.0;
        try {
            debt = rs.getDouble("debt_amount");
        } catch (SQLException ignored) { }

        // Rol ayrımı yapılarak uygun model objesi döndürülür
        if ("PERSONEL".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return new Librarian(id, username, password, fullName);
        } else if ("UYE".equalsIgnoreCase(role)) {
            return new Member(id, username, password, fullName, debt);
        }
        return new User(id, username, password, fullName, role);
    }

    // ------------------- AUTHENTICATE -------------------
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";

        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı doğrulama hatası: " + e.getMessage());
        }
        return null;
    }

    // ------------------- CRUD METOTLARI -------------------

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (Connection conn = getOpenConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = createUserFromResultSet(rs);
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Tüm kullanıcıları çekerken hata: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO Users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE Users SET username=?, password_hash=?, full_name=?, role=? WHERE id=?";

        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            pstmt.setInt(5, user.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM Users WHERE id = ?";

        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kullanıcı silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";

        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateMemberDebt(int userId, double newDebt) {
        String sql = "UPDATE Users SET debt_amount = ? WHERE id = ? AND role = 'UYE'";
        try (Connection conn = getOpenConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newDebt);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Borç güncelleme hatası: " + e.getMessage());
        }
    }
}