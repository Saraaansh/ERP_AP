
// Data access object for assessment component operations
package edu.univ.erp.data;

import edu.univ.erp.domain.Assessment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDAO {

    public boolean insert(Assessment a) throws SQLException {
        String sql = "INSERT INTO assessments (section_id, component_name, weight) VALUES (?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getSectionId());
            ps.setString(2, a.getComponentName());
            ps.setInt(3, a.getWeight());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        a.setAssessmentId(keys.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public List<Assessment> getBySection(int sectionId) {
        List<Assessment> list = new ArrayList<>();
        String sql = "SELECT * FROM assessments WHERE section_id = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Assessment(
                            rs.getInt("assessment_id"),
                            rs.getInt("section_id"),
                            rs.getString("component_name"),
                            rs.getInt("weight")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean delete(int assessmentId) throws SQLException {
        String sql = "DELETE FROM assessments WHERE assessment_id = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Assessment getById(int assessmentId) {
        String sql = "SELECT * FROM assessments WHERE assessment_id = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assessmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Assessment(
                            rs.getInt("assessment_id"),
                            rs.getInt("section_id"),
                            rs.getString("component_name"),
                            rs.getInt("weight"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
