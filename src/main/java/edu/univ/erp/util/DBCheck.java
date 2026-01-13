package edu.univ.erp.util;

import edu.univ.erp.data.AuthDBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBCheck {
    public static void main(String[] args) {
        try (Connection conn = AuthDBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT username, password_hash FROM users_auth")) {

            System.out.println("--- Users in DB ---");
            while (rs.next()) {
                System.out.println("User: " + rs.getString("username"));
                System.out.println("Hash: " + rs.getString("password_hash"));
                System.out.println("-------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
