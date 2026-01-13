
// Data access object for grade management operations
package edu.univ.erp.data;
import edu.univ.erp.domain.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    
    public boolean insert(Grade g) {
        String sql = "INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, g.getEnrollmentid());
            ps.setString(2, g.getComponent());
            ps.setDouble(3, g.getScore());
            ps.setString(4, g.getFinalgrade());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        g.setGradeId(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException ex) {
            System.err.println("Error inserting grade:");
            ex.printStackTrace();
        }

        return false;
    }

    public boolean update(Grade g) {
        String sql = "UPDATE grades SET component = ?, score = ?, final_grade = ? WHERE grade_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, g.getComponent());
            ps.setDouble(2, g.getScore());
            ps.setString(3, g.getFinalgrade());
            ps.setInt(4, g.getGradeid());

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error updating grade:");
            ex.printStackTrace();
        }

        return false;
    }

    public boolean delete(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gradeId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            System.err.println("Error deleting grade:");
            ex.printStackTrace();
        }

        return false;
    }

    public Grade getById(int gradeId) {
        String sql = "SELECT * FROM grades WHERE grade_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gradeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setEnrollmentId(rs.getInt("enrollment_id"));
                    g.setComponent(rs.getString("component"));
                    g.setScore(rs.getDouble("score"));
                    g.setFinalgrade(rs.getString("final_grade"));
                    return g;
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error fetching grade:");
            ex.printStackTrace();
        }

        return null;
    }

    public List<Grade> getByEnrollment(int enrollmentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE enrollment_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setEnrollmentId(rs.getInt("enrollment_id"));
                    g.setComponent(rs.getString("component"));
                    g.setScore(rs.getDouble("score"));
                    g.setFinalgrade(rs.getString("final_grade"));
                    list.add(g);
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error fetching grades by enrollment:");
            ex.printStackTrace();
        }

        return list;
    }

    public List<Grade> getBySection(int sectionId) {
        List<Grade> list = new ArrayList<>();

        String sql = """
                SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.final_grade
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ?
                """;

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setGradeId(rs.getInt("grade_id"));
                    g.setEnrollmentId(rs.getInt("enrollment_id"));
                    g.setComponent(rs.getString("component"));
                    g.setScore(rs.getDouble("score"));
                    g.setFinalgrade(rs.getString("final_grade"));
                    list.add(g);
                }
            }

        } catch (SQLException ex) {
            System.err.println("Error fetching grades by section:");
            ex.printStackTrace();
        }

        return list;
    }

    public List<Grade> getAll() {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM grades";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Grade g = new Grade();
                g.setGradeId(rs.getInt("grade_id"));
                g.setEnrollmentId(rs.getInt("enrollment_id"));
                g.setComponent(rs.getString("component"));
                g.setScore(rs.getDouble("score"));
                g.setFinalgrade(rs.getString("final_grade"));
                list.add(g);
            }

        } catch (SQLException ex) {
            System.err.println("Error fetching all grades:");
            ex.printStackTrace();
        }

        return list;
    }
}
