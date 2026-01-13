
// Data access object for academic term operations
package edu.univ.erp.data;

import edu.univ.erp.domain.Term;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TermDAO {

    public Term getCurrentTerm() {

        String sql = "SELECT * FROM terms WHERE start_date <= CURDATE() AND end_date >= CURDATE() LIMIT 1";

        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Term getByName(String name) {
        String sql = "SELECT * FROM terms WHERE name = ?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Term> getAll() {
        List<Term> list = new ArrayList<>();
        String sql = "SELECT * FROM terms ORDER BY start_date DESC";
        try (Connection conn = DBconnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Term t) {
        String sql = "INSERT INTO terms (name, start_date, end_date, drop_deadline) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setDate(2, Date.valueOf(t.getStartDate()));
            ps.setDate(3, Date.valueOf(t.getEndDate()));
            ps.setDate(4, Date.valueOf(t.getDropDeadline()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Term t) {
        String sql = "UPDATE terms SET name=?, start_date=?, end_date=?, drop_deadline=? WHERE term_id=?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setDate(2, Date.valueOf(t.getStartDate()));
            ps.setDate(3, Date.valueOf(t.getEndDate()));
            ps.setDate(4, Date.valueOf(t.getDropDeadline()));
            ps.setInt(5, t.getTermId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int termId) {
        String sql = "DELETE FROM terms WHERE term_id=?";
        try (Connection conn = DBconnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, termId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Term mapRow(ResultSet rs) throws SQLException {
        return new Term(
                rs.getInt("term_id"),
                rs.getString("name"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getDate("drop_deadline").toLocalDate());
    }
}
