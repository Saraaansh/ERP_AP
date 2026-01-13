// Manages user session state including logged-in user ID, role, and associated domain objects
package edu.univ.erp.auth;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.User;

public class SessionManager {
    private static int currentUserId = -1;
    private static String currentRole = null;
    private static Student currentStudent;
    private static Instructor currentInstructor;

    public static void createSession(int userId, String role) {
        currentUserId = userId;
        currentRole = role;
    }

    public static void clearSession() {
        currentUserId = -1;
        currentRole = null;
    }

    public static boolean isLoggedIn() {
        return currentUserId != -1 && currentRole != null;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static void setCurrentStudent(Student student) {
        currentStudent = student;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static void setCurrentInstructor(Instructor instructor) {
        currentInstructor = instructor;
    }

    public static Instructor getCurrentInstructor() {
        return currentInstructor;
    }

    public static boolean hasRole(String role) {
        if (currentRole == null)
            return false;
        return currentRole.equalsIgnoreCase(role);
    }

    public static String getCurrentUsername() {
        if (currentStudent != null) {
            return currentStudent.getName();
        }
        if (currentInstructor != null) {
            return currentInstructor.getName();
        }
        if (isLoggedIn()) {
            return "User";
        }
        return "User";
    }
}
