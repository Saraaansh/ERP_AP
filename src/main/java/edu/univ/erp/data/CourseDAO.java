
// Data access object for course CRUD operations
package edu.univ.erp.data;
import edu.univ.erp.domain.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CourseDAO {
    
    public boolean insert(Course c) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getCode());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCredits());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setCourseid(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting course:");
            e.printStackTrace();
        }

        return false;
    }

    public Course getById(int id) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course c = new Course();
                    c.setCourseid(rs.getInt("course_id"));
                    c.setCode(rs.getString("code"));
                    c.setTitle(rs.getString("title"));
                    c.setCredits(rs.getInt("credits"));
                    return c;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting course by ID:");
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Course c) {
        String sql = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getCode());
            ps.setString(2, c.getTitle());
            ps.setInt(3, c.getCredits());
            ps.setInt(4, c.getCourseid());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating course:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting course:");
            e.printStackTrace();
        }

        return false;
    }

    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();

        String sql = "SELECT * FROM courses";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course c = new Course();
                c.setCourseid(rs.getInt("course_id"));
                c.setCode(rs.getString("code"));
                c.setTitle(rs.getString("title"));
                c.setCredits(rs.getInt("credits"));
                list.add(c);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all courses:");
            e.printStackTrace();
        }

        return list;
    }
}
