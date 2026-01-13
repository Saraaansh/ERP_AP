package edu.univ.erp.util;

import edu.univ.erp.data.AuthDBConnection;
import edu.univ.erp.data.DBconnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class UserCheck {
    public static void main(String[] args) {
        System.out.println("--- Checking Data Integrity ---");

        // 1. Check Users
        System.out.println("\n[AUTH DB] users_auth:");
        try (Connection conn = AuthDBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT user_id, username, role FROM users_auth")) {
            System.out.printf("%-5s %-15s %-10s%n", "ID", "Username", "Role");
            System.out.println("--------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-10s%n",
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. Check Students
        System.out.println("\n[ERP DB] students:");
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT user_id, name FROM students")) {
            System.out.printf("%-5s %-20s%n", "ID", "Name");
            System.out.println("--------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s%n", rs.getInt("user_id"), rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Check Courses
        System.out.println("\n[ERP DB] courses:");
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT course_id, code, title FROM courses")) {
            System.out.printf("%-5s %-10s %-20s%n", "ID", "Code", "Title");
            System.out.println("---------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-10s %-20s%n", rs.getInt("course_id"), rs.getString("code"),
                        rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 4. Check Sections
        System.out.println("\n[ERP DB] sections:");
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT section_id, course_id, instructor_id FROM sections")) {
            System.out.printf("%-5s %-10s %-15s%n", "ID", "CourseID", "InstructorID");
            System.out.println("---------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-10d %-15d%n", rs.getInt("section_id"), rs.getInt("course_id"),
                        rs.getInt("instructor_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 5. Check Enrollments
        System.out.println("\n[ERP DB] enrollments:");
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT enrollment_id, student_id, section_id, status FROM enrollments")) {
            System.out.printf("%-5s %-10s %-10s %-10s%n", "ID", "StudentID", "SectionID", "Status");
            System.out.println("-----------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-10d %-10d %-10s%n",
                        rs.getInt("enrollment_id"),
                        rs.getInt("student_id"),
                        rs.getInt("section_id"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
