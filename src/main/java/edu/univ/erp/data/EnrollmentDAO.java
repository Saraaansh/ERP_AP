
// Data access object for student enrollment operations
package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentDAO.class);
    
    public boolean insert(Enrollment e) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, e.getStudentid());
            ps.setInt(2, e.getSectionid());
            ps.setString(3, e.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        e.setEnrollmentid(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException ex) {
            log.error("Error inserting enrollment studentId={} sectionId={}", e.getStudentid(), e.getSectionid(), ex);
        }

        return false;
    }

    public boolean exists(int studentId, int sectionId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'active'";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            log.error("Error checking enrollment existence studentId={} sectionId={}", studentId, sectionId, ex);
        }

        return false;
    }

    public boolean drop(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status = 'dropped' " +
                "WHERE student_id = ? AND section_id = ? AND status = 'active'";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            log.error("Error dropping enrollment studentId={} sectionId={}", studentId, sectionId, ex);
        }

        return false;
    }

    public Enrollment getById(int enrollmentId) {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentid(rs.getInt("enrollment_id"));
                    e.setStudentid(rs.getInt("student_id"));
                    e.setSectionid(rs.getInt("section_id"));
                    e.setStatus(rs.getString("status"));
                    return e;
                }
            }

        } catch (SQLException ex) {
            log.error("Error fetching enrollment id={}", enrollmentId, ex);
        }

        return null;
    }

    public List<Enrollment> getByStudent(int studentId) {
        List<Enrollment> list = new ArrayList<>();

        String sql = "SELECT * FROM enrollments WHERE student_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentid(rs.getInt("enrollment_id"));
                    e.setStudentid(rs.getInt("student_id"));
                    e.setSectionid(rs.getInt("section_id"));
                    e.setStatus(rs.getString("status"));
                    list.add(e);
                }
            }

        } catch (SQLException ex) {
            log.error("Error fetching student enrollments studentId={}", studentId, ex);
        }

        return list;
    }

    public List<Enrollment> getBySection(int sectionId) {
        List<Enrollment> list = new ArrayList<>();

        String sql = "SELECT * FROM enrollments WHERE section_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentid(rs.getInt("enrollment_id"));
                    e.setStudentid(rs.getInt("student_id"));
                    e.setSectionid(rs.getInt("section_id"));
                    e.setStatus(rs.getString("status"));
                    list.add(e);
                }
            }

        } catch (SQLException ex) {
            log.error("Error fetching enrollments by section sectionId={}", sectionId, ex);
        }

        return list;
    }

    public List<Enrollment> getAll() {
        List<Enrollment> list = new ArrayList<>();

        String sql = "SELECT * FROM enrollments";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setEnrollmentid(rs.getInt("enrollment_id"));
                e.setStudentid(rs.getInt("student_id"));
                e.setSectionid(rs.getInt("section_id"));
                e.setStatus(rs.getString("status"));
                list.add(e);
            }

        } catch (SQLException ex) {
            log.error("Error fetching all enrollments", ex);
        }

        return list;
    }

    public boolean isEnrolledInCourse(int studentId, int courseId) {
        String sql = "SELECT 1 FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "WHERE e.student_id = ? AND s.course_id = ? AND e.status = 'active'";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException ex) {
            log.error("Error checking course enrollment studentId={} courseId={}", studentId, courseId, ex);
        }

        return false;
    }

    public Enrollment getByStudentAndSection(int studentId, int sectionId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollmentid(rs.getInt("enrollment_id"));
                    e.setStudentid(rs.getInt("student_id"));
                    e.setSectionid(rs.getInt("section_id"));
                    e.setStatus(rs.getString("status"));
                    return e;
                }
            }

        } catch (SQLException ex) {
            log.error("Error fetching enrollment by student/section studentId={} sectionId={}", studentId, sectionId, ex);
        }

        return null;
    }

    public boolean updateStatus(int enrollmentId, String newStatus) {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, enrollmentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException ex) {
            log.error("Error updating enrollment status id={} status={}", enrollmentId, newStatus, ex);
        }

        return false;
    }
}
