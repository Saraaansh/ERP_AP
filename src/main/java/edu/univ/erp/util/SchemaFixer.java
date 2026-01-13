package edu.univ.erp.util;

import edu.univ.erp.data.AuthDBConnection;
import edu.univ.erp.data.DBconnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;

public class SchemaFixer {
    public static void main(String[] args) {
        fixAuthDB();
        fixErpDB();
    }

    private static void fixAuthDB() {
        System.out.println("Checking Auth DB schema...");
        try (Connection conn = AuthDBConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            boolean hasLockTime = false;
            boolean hasFailedAttempts = false;
            boolean hasLastLogin = false;

            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM users_auth")) {
                while (rs.next()) {
                    String col = rs.getString("Field");
                    if ("lock_time".equalsIgnoreCase(col))
                        hasLockTime = true;
                    if ("failed_attempts".equalsIgnoreCase(col))
                        hasFailedAttempts = true;
                    if ("last_login".equalsIgnoreCase(col))
                        hasLastLogin = true;
                }
            }

            if (!hasLockTime) {
                System.out.println("Adding missing column: lock_time");
                stmt.executeUpdate("ALTER TABLE users_auth ADD COLUMN lock_time DATETIME NULL");
            }
            if (!hasFailedAttempts) {
                System.out.println("Adding missing column: failed_attempts");
                stmt.executeUpdate("ALTER TABLE users_auth ADD COLUMN failed_attempts INT DEFAULT 0");
            }
            if (!hasLastLogin) {
                System.out.println("Adding missing column: last_login");
                stmt.executeUpdate("ALTER TABLE users_auth ADD COLUMN last_login DATETIME NULL");
            }
            System.out.println("Auth DB check complete.");

        } catch (SQLException e) {
            System.err.println("Error updating Auth DB: " + e.getMessage());
        }
    }

    private static void fixErpDB() {
        System.out.println("Checking ERP DB schema...");
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // 1. Check instructors table
            boolean hasOfficeHours = false;
            boolean hasInstEmail = false;
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM instructors")) {
                while (rs.next()) {
                    String col = rs.getString("Field");
                    if ("office_hours".equalsIgnoreCase(col))
                        hasOfficeHours = true;
                    if ("email".equalsIgnoreCase(col))
                        hasInstEmail = true;
                }
            }

            if (!hasOfficeHours) {
                System.out.println("Adding missing column: office_hours to instructors");
                stmt.executeUpdate("ALTER TABLE instructors ADD COLUMN office_hours VARCHAR(255)");
            }
            if (!hasInstEmail) {
                System.out.println("Adding missing column: email to instructors");
                stmt.executeUpdate("ALTER TABLE instructors ADD COLUMN email VARCHAR(255)");
            }

            // 2. Check students table
            boolean hasStudentEmail = false;
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM students")) {
                while (rs.next()) {
                    String col = rs.getString("Field");
                    if ("email".equalsIgnoreCase(col))
                        hasStudentEmail = true;
                }
            }

            if (!hasStudentEmail) {
                System.out.println("Adding missing column: email to students");
                stmt.executeUpdate("ALTER TABLE students ADD COLUMN email VARCHAR(255)");
            }

            // 3. Check courses table
            boolean hasDescription = false;
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM courses")) {
                while (rs.next()) {
                    String col = rs.getString("Field");
                    if ("description".equalsIgnoreCase(col))
                        hasDescription = true;
                }
            }

            if (!hasDescription) {
                System.out.println("Adding missing column: description to courses");
                stmt.executeUpdate("ALTER TABLE courses ADD COLUMN description TEXT");
            }

            // 4. Check terms table existence
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet tables = dbm.getTables(null, null, "terms", null)) {
                if (!tables.next()) {
                    System.out.println("Creating missing table: terms");
                    String createTermsSql = "CREATE TABLE terms (" +
                            "term_id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "name VARCHAR(100) UNIQUE NOT NULL, " +
                            "start_date DATE NOT NULL, " +
                            "end_date DATE NOT NULL, " +
                            "drop_deadline DATE NOT NULL, " +
                            "registration_deadline DATE, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")";
                    stmt.executeUpdate(createTermsSql);
                }
            }

            System.out.println("ERP DB check complete.");

        } catch (SQLException e) {
            System.err.println("Error updating ERP DB: " + e.getMessage());
        }
    }
}
