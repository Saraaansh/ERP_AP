// Database diagnostic utility for checking enrollment data integrity
package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordHasher;

public class check {
    public static void main(String[] args) {
        String stored = "$2a$10$aXqY4ze6WElGxHG7GDHteu156TAUZBHr3x0kNjbnwXvWJRxyNLUSC";
        System.out.println("Checking Admin@123 -> " + PasswordHasher.verify("Admin@123", stored));
    }
}
