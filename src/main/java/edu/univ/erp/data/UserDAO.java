
// Data access object for user authentication operations
package edu.univ.erp.data;

import edu.univ.erp.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public boolean createUser(String username, String passwordHash, String role) {
        String sql = "INSERT INTO users_auth (username, password_hash, role, status) VALUES (?, ?, ?, 'ACTIVE')";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating user:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int userId) throws Exception {
        String sql = "DELETE FROM users_auth WHERE user_id = ?";
        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(int userId, String username, String role) throws Exception {
        String sql = "UPDATE users_auth SET username=?, role=? WHERE user_id=?";
        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, role);
            ps.setInt(3, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public User getByUsername(String username) {
        String sql = "SELECT user_id, username, role, status, failed_attempts, lock_time FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp lockTimestamp = rs.getTimestamp("lock_time");
                    java.time.LocalDateTime lockTime = lockTimestamp != null ? lockTimestamp.toLocalDateTime() : null;

                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getInt("failed_attempts"),
                            lockTime);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username:");
            e.printStackTrace();
        }

        return null;
    }

    public String getPasswordHash(String username) {
        String sql = "SELECT password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching password hash:");
            e.printStackTrace();
        }

        return null;
    }

    public User getById(int userId) {
        String sql = "SELECT user_id, username, role, status, failed_attempts, lock_time FROM users_auth WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp lockTimestamp = rs.getTimestamp("lock_time");
                    java.time.LocalDateTime lockTime = lockTimestamp != null ? lockTimestamp.toLocalDateTime() : null;

                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getInt("failed_attempts"),
                            lockTime);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by ID:");
            e.printStackTrace();
        }

        return null;
    }

    public boolean exists(String username) {
        String sql = "SELECT 1 FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error checking if user exists:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean disableUser(int userId) {
        String sql = "UPDATE users_auth SET status = 'DISABLED' WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error disabling user:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean activateUser(int userId) {
        String sql = "UPDATE users_auth SET status = 'ACTIVE' WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error enabling user:");
            e.printStackTrace();
        }

        return false;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();

        String sql = "SELECT user_id, username, role, status FROM users_auth";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status")));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all users:");
            e.printStackTrace();
        }

        return list;
    }

    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean incrementFailedAttempts(String username) {
        String sql = "UPDATE users_auth SET failed_attempts = failed_attempts + 1 WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error incrementing failed attempts:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean lockAccount(String username) {
        String sql = "UPDATE users_auth SET lock_time = DATE_ADD(NOW(), INTERVAL 15 MINUTE), failed_attempts = 5 WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error locking account:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean resetFailedAttempts(String username) {
        String sql = "UPDATE users_auth SET failed_attempts = 0, lock_time = NULL WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error resetting failed attempts:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean isAccountLocked(String username) {
        String sql = "SELECT lock_time FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Timestamp lockTimestamp = rs.getTimestamp("lock_time");
                    if (lockTimestamp == null) {
                        return false;
                    }

                    java.time.LocalDateTime lockTime = lockTimestamp.toLocalDateTime();
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();

                    return lockTime.isAfter(now);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking account lock status:");
            e.printStackTrace();
        }

        return false;
    }

    public int getFailedAttempts(String username) {
        String sql = "SELECT failed_attempts FROM users_auth WHERE username = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("failed_attempts");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching failed attempts:");
            e.printStackTrace();
        }

        return 0;
    }

    public List<User> getAllUsersWithLockStatus() {
        List<User> list = new ArrayList<>();

        String sql = "SELECT user_id, username, role, status, failed_attempts, lock_time FROM users_auth ORDER BY user_id";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                java.sql.Timestamp lockTimestamp = rs.getTimestamp("lock_time");
                java.time.LocalDateTime lockTime = lockTimestamp != null ? lockTimestamp.toLocalDateTime() : null;

                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getInt("failed_attempts"),
                        lockTime);

                list.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users with lock status:");
            e.printStackTrace();
        }

        return list;
    }

    public boolean manuallyBlockUser(int userId) {
        String sql = "UPDATE users_auth SET status = 'DISABLED' WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error manually blocking user:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean manuallyUnblockUser(int userId) {
        String sql = "UPDATE users_auth SET status = 'ACTIVE', lock_time = NULL, failed_attempts = 0 WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error manually unblocking user:");
            e.printStackTrace();
        }

        return false;
    }
}
