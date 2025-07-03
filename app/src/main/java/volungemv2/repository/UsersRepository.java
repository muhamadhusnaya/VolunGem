package volungemv2.repository;

import volungemv2.models.Users;
import volungemv2.utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersRepository {
    
    // Method untuk mencari user berdasarkan username
    public Users findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding user by username: " + username, e);
        }
        
        return null;
    }

    // Method untuk mencari user berdasarkan email
    public Users findByUsernameOrEmail(String identifier) {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)";
        try (Connection connection = DBUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding user by identifier: " + identifier, e);
        }
        
        return null;
    }

    // Method untuk menyimpan user baru
    public boolean save(Users user) {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user: " + user.getUsername(), e);
        }
    }

    // Method untuk update user
    public boolean update(Users user) {
        String sql = "UPDATE users SET password = ?, email = ?, role = ? WHERE username = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getUsername());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating user: " + user.getUsername(), e);
        }
    }

    // Method untuk menghapus user
    public boolean delete(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting user: " + username, e);
        }
    }

    // Method untuk mendapatkan semua users
    public List<Users> findAll() {
        List<Users> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding all users", e);
        }
        
        return users;
    }

    // Method untuk mendapatkan users berdasarkan role
    public List<Users> findByRole(String role) {
        List<Users> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error finding users by role: " + role, e);
        }
        
        return users;
    }

    // Method untuk cek apakah username sudah ada
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking username exists: " + username, e);
        }
        
        return false;
    }

    // Method untuk cek apakah email sudah ada
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error checking email exists: " + email, e);
        }
        
        return false;
    }

    // Method untuk update password saja
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating password for user: " + username, e);
        }
    }

    // Helper method untuk mapping ResultSet ke Users object
    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        try {
            // Debug: Print semua kolom yang tersedia
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            System.out.println("=== DATABASE COLUMNS DEBUG ===");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnValue = rs.getString(i);
                System.out.println("Column " + i + ": " + columnName + " = '" + columnValue + "'");
            }
            
            // Coba mapping dengan nama kolom yang benar
            String username = rs.getString("username");
            String password = rs.getString("password");
            String email = rs.getString("email");
            String role = rs.getString("role");
            
            System.out.println("Mapped values:");
            System.out.println("  Username: '" + username + "'");
            System.out.println("  Password: '" + password + "'");
            System.out.println("  Email: '" + email + "'");
            System.out.println("  Role: '" + role + "'");
            
            return new Users(username, password, email, role);
            
        } catch (SQLException e) {
            System.out.println("Error in mapping: " + e.getMessage());
            
            // Jika mapping berdasarkan nama kolom gagal, coba berdasarkan index
            System.out.println("Trying index-based mapping...");
            
            // Asumsi urutan kolom: id, username, password, email, role
            // Sesuaikan index berdasarkan struktur tabel Anda
            try {
                String username = rs.getString(2); // kolom ke-2
                String password = rs.getString(3); // kolom ke-3  
                String email = rs.getString(4);    // kolom ke-4
                String role = rs.getString(5);     // kolom ke-5
                
                System.out.println("Index-based mapping:");
                System.out.println("  Username (col 2): '" + username + "'");
                System.out.println("  Password (col 3): '" + password + "'");
                System.out.println("  Email (col 4): '" + email + "'");
                System.out.println("  Role (col 5): '" + role + "'");
                
                return new Users(username, password, email, role);
                
            } catch (SQLException e2) {
                throw new SQLException("Both name-based and index-based mapping failed", e2);
            }
        }
    }
}