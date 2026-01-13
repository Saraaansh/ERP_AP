
// Data access object for section CRUD operations
package edu.univ.erp.data;

import edu.univ.erp.domain.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {
    
    public Section getById(int sectionId) {
        String sql = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Section s = new Section();
                    s.setSectionid(rs.getInt("section_id"));
                    s.setCourseid(rs.getInt("course_id"));
                    s.setInstructorid(rs.getInt("instructor_id"));
                    s.setDaytime(rs.getString("day_time"));
                    s.setRoom(rs.getString("room"));
                    s.setCapacity(rs.getInt("capacity"));
                    s.setSemester(rs.getString("semester"));
                    s.setYear(rs.getInt("year"));
                    return s;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error in getById:");
            e.printStackTrace();
        }

        return null;
    }

    public boolean createSection(Section s) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, s.getCourseid());
            ps.setInt(2, s.getInstructorid());
            ps.setString(3, s.getDaytime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setString(6, s.getSemester());
            ps.setInt(7, s.getYear());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        s.setSectionid(keys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error in createSection:");
            e.printStackTrace();
        }

        return false;
    }

    public List<Section> getByInstructor(int instructorId) {
        List<Section> list = new ArrayList<>();

        String sql = "SELECT * FROM sections WHERE instructor_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Section s = new Section();
                    s.setSectionid(rs.getInt("section_id"));
                    s.setCourseid(rs.getInt("course_id"));
                    s.setInstructorid(rs.getInt("instructor_id"));
                    s.setCapacity(rs.getInt("capacity"));
                    s.setDaytime(rs.getString("day_time"));
                    s.setRoom(rs.getString("room"));
                    s.setSemester(rs.getString("semester"));
                    s.setYear(rs.getInt("year"));
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertSection(Section s) throws Exception {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseid());
            ps.setInt(2, s.getInstructorid());
            ps.setString(3, s.getDaytime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setString(6, s.getSemester());
            ps.setInt(7, s.getYear());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateSection(Section s) throws Exception {
        String sql = "UPDATE sections SET course_id=?, instructor_id=?, day_time=?, room=?, capacity=?, semester=?, year=? WHERE section_id=?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCourseid());
            ps.setInt(2, s.getInstructorid());
            ps.setString(3, s.getDaytime());
            ps.setString(4, s.getRoom());
            ps.setInt(5, s.getCapacity());
            ps.setString(6, s.getSemester());
            ps.setInt(7, s.getYear());
            ps.setInt(8, s.getSectionid());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSection(int sectionId) throws Exception {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean assignInstructor(int sectionId, int instructorId) throws Exception {
        String sql = "UPDATE sections SET instructor_id=? WHERE section_id=?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;
        }
    }

    public int countEnrolled(int sectionId) throws Exception {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'active'";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        String sql = "SELECT * FROM sections";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Section s = new Section();
                s.setSectionid(rs.getInt("section_id"));
                s.setCourseid(rs.getInt("course_id"));
                s.setInstructorid(rs.getInt("instructor_id"));
                s.setDaytime(rs.getString("day_time"));
                s.setRoom(rs.getString("room"));
                s.setCapacity(rs.getInt("capacity"));
                s.setSemester(rs.getString("semester"));
                s.setYear(rs.getInt("year"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
