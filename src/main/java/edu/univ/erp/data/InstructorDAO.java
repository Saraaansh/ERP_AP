
// Data access object for instructor CRUD operations
package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public boolean insert(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, name, department, email) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructor.getUserid());
            ps.setString(2, instructor.getName());
            ps.setString(3, instructor.getDepartment());
            ps.setString(4, instructor.getEmail());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting instructor: " + e.getMessage());
            return false;
        }
    }

    public List<Instructor> getAllInstructorProfiles() throws Exception {
        List<Instructor> list = new ArrayList<>();

        String sql = "SELECT user_id, name, department, email FROM instructors";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instructor ins = new Instructor();
                ins.setUserid(rs.getInt("user_id"));
                ins.setName(rs.getString("name"));
                ins.setDepartment(rs.getString("department"));
                ins.setEmail(rs.getString("email"));
                list.add(ins);
            }
        }
        return list;
    }

    public boolean update(Instructor instructor) {
        String sql = "UPDATE instructors SET name = ?, department = ?, email = ? WHERE user_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, instructor.getName());
            ps.setString(2, instructor.getDepartment());
            ps.setString(3, instructor.getEmail());
            ps.setInt(4, instructor.getUserid());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating instructor: " + e.getMessage());
            return false;
        }
    }

    public Instructor getById(int userId) {
        String sql = "SELECT user_id, name, department, email FROM instructors WHERE user_id = ?";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Instructor i = new Instructor();
                i.setUserid(rs.getInt("user_id"));
                i.setName(rs.getString("name"));
                i.setDepartment(rs.getString("department"));
                i.setEmail(rs.getString("email"));
                return i;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructor: " + e.getMessage());
        }

        return null;
    }

    public boolean deleteByUserId(int userId) throws Exception {
        String sql = "DELETE FROM instructors WHERE user_id = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Instructor> getAll() {
        List<Instructor> list = new ArrayList<>();
        String sql = "SELECT user_id, name, department, email FROM instructors";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Instructor i = new Instructor();
                i.setUserid(rs.getInt("user_id"));
                i.setName(rs.getString("name"));
                i.setDepartment(rs.getString("department"));
                i.setEmail(rs.getString("email"));
                list.add(i);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching instructors: " + e.getMessage());
        }

        return list;
    }

}
