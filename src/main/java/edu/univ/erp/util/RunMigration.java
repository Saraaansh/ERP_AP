package edu.univ.erp.util;

import edu.univ.erp.data.DBconnection;
import java.sql.Connection;
import java.sql.Statement;

public class RunMigration {

    public static void main(String[] args) {
        System.out.println("Starting database migration...");

        String createAssessmentsTable = """
                    CREATE TABLE IF NOT EXISTS assessments (
                        assessment_id INT AUTO_INCREMENT PRIMARY KEY,
                        section_id INT NOT NULL,
                        component_name VARCHAR(100) NOT NULL,
                        weight INT NOT NULL CHECK (weight > 0 AND weight <= 100),
                        FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
                        UNIQUE KEY unique_section_component (section_id, component_name)
                    );
                """;

        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement()) {

            System.out.println("Connected to database.");

            stmt.execute(createAssessmentsTable);
            System.out.println("Successfully created 'assessments' table.");

        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
