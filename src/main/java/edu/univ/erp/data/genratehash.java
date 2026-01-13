// Utility to generate BCrypt password hashes for testing
package edu.univ.erp.data;
import edu.univ.erp.auth.PasswordHasher;
public class genratehash {
    public static void main(String[] args) {
        String hash = PasswordHasher.hash("password123");
        System.out.println("New Hash: " + hash);
    }
}
