package com.kutuphane.repository;

import com.kutuphane.model.Librarian;
import com.kutuphane.model.Member;
import com.kutuphane.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

// FACTORY DESENİ: Rol tipine göre doğru User nesnesini oluşturan sınıf.
public class UserRepositoryFactory {

    /**
     * Veritabanından gelen ResultSet verisine göre uygun User (Member veya Librarian) nesnesini oluşturur.
     */
    public static User createUserFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String role = rs.getString("role");

        if ("PERSONEL".equalsIgnoreCase(role)) {
            // Personel nesnesini oluştur
            return new Librarian(id, username, password, fullName);
        } else if ("UYE".equalsIgnoreCase(role)) {
            // Üye nesnesini oluştur (Ek olarak borç bilgisi çekilebilir)
            // Varsayılan borç bilgisi 0.0 olarak atanmıştır.
            double fineDebt = rs.getDouble("fine_debt"); // Eğer Members tablosu varsa oradan çekilmeli
            return new Member(id, username, password, fullName, fineDebt);
        }

        throw new IllegalArgumentException("Bilinmeyen kullanıcı rolü: " + role);
    }

    // İleride farklı Repository'ler için Factory metotları buraya eklenebilir.
}