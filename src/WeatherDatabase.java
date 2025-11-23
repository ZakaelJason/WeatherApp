import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherDatabase {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/weather_db";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection connection;

    public WeatherDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("Gagal koneksi database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method helper untuk memastikan koneksi masih hidup
    private void checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menambahkan ke history
    public void addToHistory(String city) {
        checkConnection();

        String sql = "INSERT INTO search_history (city_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menghapus semua data history
    public void clearHistory() {
        checkConnection();

        String sql = "DELETE FROM search_history";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mendapatkan list history (terbaru di atas)
    public List<String> getHistory() {
        checkConnection();

        List<String> history = new ArrayList<>();
        String sql = "SELECT city_name FROM search_history ORDER BY search_time DESC LIMIT 20"; // Batasi 20 terakhir
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    // Menambah ke favorit
    public boolean addFavorite(String city) {
        checkConnection();

        String sql = "INSERT IGNORE INTO favorite_cities (city_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, city);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Menghapus dari favorit
    public void removeFavorite(String city) {
        checkConnection();
        String sql = "DELETE FROM favorite_cities WHERE city_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cek apakah kota adalah favorit
    public boolean isFavorite(String city) {
        checkConnection();
        String sql = "SELECT id FROM favorite_cities WHERE city_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, city);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mendapatkan list favorit
    public List<String> getFavorites() {
        checkConnection();
        List<String> favorites = new ArrayList<>();
        String sql = "SELECT city_name FROM favorite_cities ORDER BY city_name ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                favorites.add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }

    // Method untuk menutup koneksi saat app keluar
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi ke Database Sudah ditutup!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}