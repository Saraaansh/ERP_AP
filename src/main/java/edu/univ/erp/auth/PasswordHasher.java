// Provides BCrypt password hashing and verification functionality
package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(12));
    }

    public static boolean verify(String plain, String hash) {
        if (hash == null)
            return false;
        return BCrypt.checkpw(plain, hash);
    }
}
