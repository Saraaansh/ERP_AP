// Handles user authentication, login throttling with 5-attempt lockout, and session management
package edu.univ.erp.auth;

import edu.univ.erp.data.AuthDBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.univ.erp.domain.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthManager {
    private static final Logger log = LoggerFactory.getLogger(AuthManager.class);
    public User login(String username, String password) {
        String sql = "SELECT user_id, username, password_hash, role, status, failed_attempts, lock_time FROM users_auth WHERE username = ?";
        edu.univ.erp.data.UserDAO userDAO = new edu.univ.erp.data.UserDAO();

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    log.warn("Username does not exist: {}", username);
                    return null;
                }

                int userId = rs.getInt("user_id");
                String storedHash = rs.getString("password_hash");
                String role = rs.getString("role");
                String status = rs.getString("status");
                java.sql.Timestamp lockTimestamp = rs.getTimestamp("lock_time");

                if (lockTimestamp != null) {
                    java.time.LocalDateTime lockTime = lockTimestamp.toLocalDateTime();
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();

                    if (lockTime.isAfter(now)) {
                        long minutesRemaining = java.time.Duration.between(now, lockTime).toMinutes();
                        log.warn("Account locked. {} minutes remaining for {}", minutesRemaining, username);
                        return null;
                    } else {
                        userDAO.resetFailedAttempts(username);
                    }
                }

                if (status == null || !"ACTIVE".equalsIgnoreCase(status)) {
                    log.warn("User account disabled: {}", username);
                    return null;
                }

                if (storedHash == null || storedHash.isEmpty()) {
                    log.error("No password hash set for user {}", username);
                    return null;
                }

                if (!PasswordHasher.verify(password, storedHash)) {
                    log.warn("Wrong password for {}", username);

                    userDAO.incrementFailedAttempts(username);
                    int newFailedAttempts = userDAO.getFailedAttempts(username);

                    if (newFailedAttempts >= 5) {
                        userDAO.lockAccount(username);
                        log.warn("Account locked due to 5 failed attempts for {}", username);
                    } else {
                        log.warn("Failed login attempt for {} ({} attempts remaining)", username, (5 - newFailedAttempts));
                    }

                    return null;
                }

                userDAO.resetFailedAttempts(username);

                updateLastLogin(userId);

                User user = new User(userId, username, role, status, 0, null);

                SessionManager.createSession(userId, role);

                log.info("Login successful for {} (id={})", username, userId);
                return user;
            }

        } catch (SQLException e) {
            log.error("Error during login for {}", username, e);
        }

        return null;
    }

    public boolean createUser(String username, String role, String plainPassword) {
        String hashed = PasswordHasher.hash(plainPassword);

        String sql = "INSERT INTO users_auth (username, password_hash, role, status) VALUES (?, ?, ?, 'ACTIVE')";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, role);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            log.error("Error creating user {}", username, e);
        }

        return false;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = NOW() WHERE user_id = ?";

        try (Connection conn = AuthDBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (SQLException e) {
            log.error("Error updating last_login for userId {}", userId, e);
        }
    }

    public void logout() {
        SessionManager.clearSession();
        log.info("Logged out successfully");
    }
}
