package edu.univ.erp.ui.util;

import edu.univ.erp.data.AuthDBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Temporary utility class to update the database structure.
 * Adds failed_attempts and lock_time columns to users_auth table.
 * 
 * Run this class once to update your database schema.
 */
public class DatabaseFixer {
    
    public static void main(String[] args) {
        System.out.println("🔧 Starting database update...");
        
        try (Connection conn = AuthDBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            System.out.println("✅ Connected to auth_db successfully!");
            
            // Add failed_attempts column
            try {
                String sql1 = "ALTER TABLE users_auth ADD COLUMN failed_attempts INT DEFAULT 0";
                stmt.executeUpdate(sql1);
                System.out.println("✅ Added column: failed_attempts");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || 
                    e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️  Column 'failed_attempts' already exists, skipping...");
                } else {
                    throw e; // Re-throw if it's a different error
                }
            }
            
            // Add lock_time column
            try {
                String sql2 = "ALTER TABLE users_auth ADD COLUMN lock_time DATETIME NULL";
                stmt.executeUpdate(sql2);
                System.out.println("✅ Added column: lock_time");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || 
                    e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️  Column 'lock_time' already exists, skipping...");
                } else {
                    throw e; // Re-throw if it's a different error
                }
            }
            
            // Create index on lock_time for better performance
            try {
                String sql3 = "CREATE INDEX idx_lock_time ON users_auth(lock_time)";
                stmt.executeUpdate(sql3);
                System.out.println("✅ Created index: idx_lock_time");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate key name") || 
                    e.getMessage().contains("already exists")) {
                    System.out.println("ℹ️  Index 'idx_lock_time' already exists, skipping...");
                } else {
                    // Index creation failure is not critical, just log it
                    System.out.println("⚠️  Could not create index (non-critical): " + e.getMessage());
                }
            }
            
            System.out.println("\n✅ Database Updated Successfully!");
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating database:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

