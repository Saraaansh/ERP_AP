
// Data access object for student CRUD operations
package edu.univ.erp.data;
import edu.univ.erp.domain.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class StudentDAO {
    public boolean insert(Student s) {
        String sql = """
                INSERT INTO students (user_id, name, roll_no, program, year)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getUserid());
            ps.setString(2, s.getName());
            ps.setString(3, s.getRollno());
            ps.setString(4, s.getProgram());
            ps.setInt(5, s.getYear());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting student:");
            e.printStackTrace();
        }

        return false;
    }

    public Student getById(int id) {
        String sql = "SELECT * FROM students WHERE user_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Student s = new Student();
                    s.setUserid(rs.getInt("user_id"));
                    s.setName(rs.getString("name"));
                    s.setRollno(rs.getString("roll_no"));
                    s.setProgram(rs.getString("program"));
                    s.setYear(rs.getInt("year"));
                    return s;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching student:");
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Student s) {
        String sql = """
                UPDATE students
                SET name = ?, roll_no = ?, program = ?, year = ?
                WHERE user_id = ?
                """;

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getName());
            ps.setString(2, s.getRollno());
            ps.setString(3, s.getProgram());
            ps.setInt(4, s.getYear());
            ps.setInt(5, s.getUserid());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating student:");
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE user_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting student:");
            e.printStackTrace();
        }

        return false;
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM students ORDER BY roll_no";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student s = new Student();
                s.setUserid(rs.getInt("user_id"));
                s.setName(rs.getString("name"));
                s.setRollno(rs.getString("roll_no"));
                s.setProgram(rs.getString("program"));
                s.setYear(rs.getInt("year"));
                list.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all students:");
            e.printStackTrace();
        }

        return list;
    }

    public Student getByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, rollNo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setUserid(rs.getInt("user_id"));
                    s.setName(rs.getString("name"));
                    s.setRollno(rs.getString("roll_no"));
                    s.setProgram(rs.getString("program"));
                    s.setYear(rs.getInt("year"));
                    return s;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching by roll number:");
            e.printStackTrace();
        }

        return null;
    }
    public boolean deleteByUserId(int userId) throws Exception {
        String sql = "DELETE FROM students WHERE user_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Student> getByProgram(String program) {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM students WHERE program = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, program);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setUserid(rs.getInt("user_id"));
                    s.setName(rs.getString("name"));
                    s.setRollno(rs.getString("roll_no"));
                    s.setProgram(rs.getString("program"));
                    s.setYear(rs.getInt("year"));
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error filtering students:");
            e.printStackTrace();
        }

        return list;
    }
}
