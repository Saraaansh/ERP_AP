// Business logic layer for authentication and password management
package edu.univ.erp.service;

import edu.univ.erp.auth.AuthManager;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.UserDAO;
import edu.univ.erp.domain.User;
import edu.univ.erp.access.AccessControl;

public class AuthService {
    private final AuthManager authManager = new AuthManager();
    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws Exception {

        if (username == null || username.isBlank())
            throw new Exception("Username is required.");

        if (password == null || password.isBlank())
            throw new Exception("Password is required.");

        User user = authManager.login(username, password);

        if (user == null)
            throw new Exception("Invalid credentials.");

        return user;
    }

    public void logout() {
        authManager.logout();
    }

    public boolean createUser(String username, String role, String plainPassword) throws Exception {

        if (!AccessControl.isAllowedForCurrentUser("createUser")) {
            throw new Exception("Only ADMIN can create users.");
        }

        if (username == null || username.isBlank())
            throw new Exception("Username cannot be empty.");

        if (plainPassword == null || plainPassword.length() < 4)
            throw new Exception("Password must be at least 4 characters.");

        if (role == null || role.isBlank())
            throw new Exception("User role must be provided.");

        if (userDAO.exists(username)) {
            throw new Exception("Username already exists.");
        }

        return authManager.createUser(username, role, plainPassword);
    }

    public boolean changePassword(String oldPass, String newPass) throws Exception {

        if (!SessionManager.isLoggedIn()) {
            throw new Exception("Not logged in.");
        }

        int userId = SessionManager.getCurrentUserId();
        User user = userDAO.getById(userId);

        if (user == null)
            throw new Exception("User not found.");

        String currentHash = userDAO.getPasswordHash(user.getUsername());

        if (!PasswordHasher.verify(oldPass, currentHash)) {
            throw new Exception("Old password incorrect.");
        }

        if (newPass.length() < 4) {
            throw new Exception("New password must be at least 4 characters.");
        }

        String newHash = PasswordHasher.hash(newPass);

        return userDAO.updatePassword(userId, newHash);
    }

    public boolean disableUser(int targetUserId) throws Exception {

        if (!AccessControl.isAllowedForCurrentUser("disableUser")) {
            throw new Exception("Only ADMIN can disable users.");
        }

        return userDAO.disableUser(targetUserId);
    }

    public boolean enableUser(int targetUserId) throws Exception {

        if (!AccessControl.isAllowedForCurrentUser("enableUser")) {
            throw new Exception("Only ADMIN can enable users.");
        }

        return userDAO.activateUser(targetUserId);
    }

    public User getCurrentUser() {
        int id = SessionManager.getCurrentUserId();
        if (id == -1)
            return null;
        return userDAO.getById(id);
    }

    public int getFailedAttempts(String username) {
        return userDAO.getFailedAttempts(username);
    }

    public boolean isAccountLocked(String username) {
        return userDAO.isAccountLocked(username);
    }

}
