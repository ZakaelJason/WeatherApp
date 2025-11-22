import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherDatabase {
    // GANTI sesuai konfigurasi MySQL Anda
    private static final String DB_URL = "jdbc:mysql://localhost:3306/weather_db";
    private static final String USER = "root"; // Default username XAMPP/MySQL
    private static final String PASS = "";     // Default password kosong

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Menambahkan ke history
    public void addToHistory(String city) {
        String sql = "INSERT INTO search_history (city_name) VALUES (?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Menghapus semua data history
    public void clearHistory() {
        String sql = "DELETE FROM search_history";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mendapatkan list history (terbaru di atas)
    public List<String> getHistory() {
        List<String> history = new ArrayList<>();
        String sql = "SELECT city_name FROM search_history ORDER BY search_time DESC LIMIT 20"; // Batasi 20 terakhir
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
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
        String sql = "INSERT IGNORE INTO favorite_cities (city_name) VALUES (?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        String sql = "DELETE FROM favorite_cities WHERE city_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cek apakah kota adalah favorit
    public boolean isFavorite(String city) {
        String sql = "SELECT id FROM favorite_cities WHERE city_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
        List<String> favorites = new ArrayList<>();
        String sql = "SELECT city_name FROM favorite_cities ORDER BY city_name ASC";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                favorites.add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }
}