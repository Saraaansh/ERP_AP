package edu.univ.erp.util;

import edu.univ.erp.data.AuthDBConnection;
import edu.univ.erp.data.DBconnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataSeeder {
    public static void main(String[] args) {
        System.out.println("Seeding ERP database with sample data...");

        Map<String, Integer> userIds = new HashMap<>();

        // 1. Fetch User IDs from Auth DB
        System.out.println("Fetching user IDs from auth_db...");
        try (Connection conn = AuthDBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT username, user_id FROM users_auth")) {

            while (rs.next()) {
                userIds.put(rs.getString("username"), rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to auth_db.");
            return;
        }

        // Check if required users exist
        String[] requiredUsers = { "inst1", "inst2", "stu1", "stu2", "stu3" };
        boolean missingUsers = false;
        for (String user : requiredUsers) {
            if (!userIds.containsKey(user)) {
                System.err.println("Error: User '" + user + "' not found in auth_db. Please run DBFixer first.");
                missingUsers = true;
            }
        }
        if (missingUsers)
            return;

        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // 2. Instructors
            int inst1Id = userIds.get("inst1");
            int inst2Id = userIds.get("inst2");

            // Use REPLACE INTO to update existing records or insert new ones, handling ID
            // changes
            String instSql = "REPLACE INTO instructors (user_id, name, department, email, office_hours) VALUES " +
                    "(" + inst1Id
                    + ", 'Dr. Sarah Williams', 'Computer Science', 'swilliams@university.edu', 'MWF 2-4 PM'), " +
                    "(" + inst2Id
                    + ", 'Prof. John Davis', 'Information Technology', 'jdavis@university.edu', 'TTh 10-12 PM')";
            stmt.executeUpdate(instSql);
            System.out.println("Seeded Instructors (Synced IDs: inst1=" + inst1Id + ", inst2=" + inst2Id + ")");

            // 3. Students
            int stu1Id = userIds.get("stu1");
            int stu2Id = userIds.get("stu2");
            int stu3Id = userIds.get("stu3");

            String stuSql = "REPLACE INTO students (user_id, name, roll_no, program, year, email) VALUES " +
                    "(" + stu1Id + ", 'Alice Johnson', 'STU001', 'Computer Science', 2, 'alice@university.edu'), " +
                    "(" + stu2Id + ", 'Bob Smith', 'STU002', 'Computer Science', 2, 'bob@university.edu'), " +
                    "(" + stu3Id
                    + ", 'Charlie Brown', 'STU003', 'Information Technology', 3, 'charlie@university.edu')";
            stmt.executeUpdate(stuSql);
            System.out.println(
                    "Seeded Students (Synced IDs: stu1=" + stu1Id + ", stu2=" + stu2Id + ", stu3=" + stu3Id + ")");

            // 4. Courses
            stmt.executeUpdate("INSERT IGNORE INTO courses (course_id, code, title, credits, description) VALUES " +
                    "(1, 'CS101', 'Introduction to Programming', 3, 'Basic programming concepts using Java'), " +
                    "(2, 'CS201', 'Data Structures', 4, 'Advanced data structures and algorithms'), " +
                    "(3, 'CS301', 'Database Systems', 3, 'Relational database design and SQL'), " +
                    "(4, 'IT101', 'Web Development', 3, 'HTML, CSS, JavaScript and modern web frameworks')");
            System.out.println("Seeded Courses.");

            // 5. Terms
            stmt.executeUpdate(
                    "INSERT IGNORE INTO terms (term_id, name, start_date, end_date, drop_deadline, registration_deadline) VALUES "
                            +
                            "(1, 'Spring 2025', '2025-01-15', '2025-05-15', '2025-02-15', '2025-01-10')");
            System.out.println("Seeded Terms.");

            // 6. Sections
            String secSql = "REPLACE INTO sections (section_id, course_id, instructor_id, capacity, day_time, room, semester, year) VALUES "
                    +
                    "(1, 1, " + inst1Id + ", 30, 'MWF 9:00-10:00', 'Room 101', 'Spring', 2025), " +
                    "(2, 2, " + inst1Id + ", 25, 'TTh 11:00-12:30', 'Room 102', 'Spring', 2025), " +
                    "(3, 3, " + inst2Id + ", 30, 'MWF 2:00-3:00', 'Room 201', 'Spring', 2025), " +
                    "(4, 4, " + inst2Id + ", 20, 'TTh 9:00-10:30', 'Room 103', 'Spring', 2025)";
            stmt.executeUpdate(secSql);
            System.out.println("Seeded Sections.");

            // 7. Enrollments
            // Clear old enrollments for these students to avoid duplicates if IDs changed
            stmt.executeUpdate(
                    "DELETE FROM enrollments WHERE student_id IN (" + stu1Id + ", " + stu2Id + ", " + stu3Id + ")");

            String enrollSql = "INSERT IGNORE INTO enrollments (student_id, section_id, status) VALUES " +
                    "(" + stu1Id + ", 1, 'active'), " +
                    "(" + stu1Id + ", 2, 'active'), " +
                    "(" + stu2Id + ", 1, 'active'), " +
                    "(" + stu2Id + ", 3, 'active'), " +
                    "(" + stu3Id + ", 4, 'active')";
            stmt.executeUpdate(enrollSql);
            System.out.println("Seeded Enrollments.");

            System.out.println("Data seeding complete!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error seeding data: " + e.getMessage());
        }
    }
}
