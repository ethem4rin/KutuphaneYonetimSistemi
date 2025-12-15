package com.kutuphane.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:kutuphane_db.sqlite";
    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = DriverManager.getConnection(URL)) {
                initializeDatabase(conn);
            }
            System.out.println("Veritabanı bağlantısı başarıyla açıldı.");
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Sürücüsü bulunamadı: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void initializeDatabase(Connection conn) throws SQLException {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "full_name TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "debt_amount REAL DEFAULT 0.0" +
                ");";

        String sqlBooks = "CREATE TABLE IF NOT EXISTS Books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "publisher TEXT," +
                "publication_year INTEGER," +
                "isbn TEXT UNIQUE," +
                "total_quantity INTEGER NOT NULL," +
                "available_quantity INTEGER NOT NULL," +
                "category_id INTEGER," +
                "location TEXT" +
                ");";

        String sqlLoans = "CREATE TABLE IF NOT EXISTS Loans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "book_id INTEGER NOT NULL,"
                + "user_id INTEGER NOT NULL,"
                + "loan_date DATE NOT NULL,"
                + "due_date DATE NOT NULL,"
                + "return_date DATE,"
                + "fine_amount REAL,"
                + "FOREIGN KEY(book_id) REFERENCES Books(id),"
                + "FOREIGN KEY(user_id) REFERENCES Users(id)"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlBooks);
            stmt.execute(sqlLoans);

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM Users WHERE username='admin'")) {
                if (!rs.next()) {
                    stmt.executeUpdate("INSERT INTO Users (username, password_hash, full_name, role) VALUES ('admin', 'pass123', 'Sistem Yöneticisi', 'ADMIN')");
                }
            }
        }
    }
}