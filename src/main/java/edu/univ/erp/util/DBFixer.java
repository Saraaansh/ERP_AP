package edu.univ.erp.util;

import edu.univ.erp.data.AuthDBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class DBFixer {
    public static void main(String[] args) {
        // Valid BCrypt hash for "password123"
        String hash = "$2a$12$9ZCjIstLa96W2STi6IFqQeD6UL4N96CXRDIDRrNrOuGSPVrl22/96";

        System.out.println("--- Current Users in DB ---");
        try (Connection conn = AuthDBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users_auth")) {
            boolean hasUsers = false;
            while (rs.next()) {
                System.out.println("Found user: " + rs.getString("username"));
                hasUsers = true;
            }
            if (!hasUsers) {
                System.out.println("(No users found)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("---------------------------");

        String[][] users = {
                { "admin1", "ADMIN" },
                { "inst1", "INSTRUCTOR" },
                { "inst2", "INSTRUCTOR" },
                { "stu1", "STUDENT" },
                { "stu2", "STUDENT" },
                { "stu3", "STUDENT" }
        };

        try (Connection conn = AuthDBConnection.getConnection()) {
            String updateSql = "UPDATE users_auth SET password_hash = ? WHERE username = ?";
            String insertSql = "INSERT INTO users_auth (username, password_hash, role, status) VALUES (?, ?, ?, 'ACTIVE')";

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql);
                 PreparedStatement psInsert = conn.prepareStatement(insertSql)) {

                for (String[] user : users) {
                    String username = user[0];
                    String role = user[1];

                    // Try Update
                    psUpdate.setString(1, hash);
                    psUpdate.setString(2, username);
                    int rows = psUpdate.executeUpdate();

                    if (rows > 0) {
                        System.out.println("Updated password for user: " + username);
                    } else {
                        System.out.println("User not found, inserting: " + username);
                        // Try Insert
                        try {
                            psInsert.setString(1, username);
                            psInsert.setString(2, hash);
                            psInsert.setString(3, role);
                            psInsert.executeUpdate();
                            System.out.println("Inserted user: " + username);
                        } catch (SQLException ex) {
                            System.err.println("Failed to insert " + username + ": " + ex.getMessage());
                        }
                    }
                }
            }
            System.out.println("Database fix complete.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error updating database. Make sure the database is running and configured correctly.");
        }
    }
}
