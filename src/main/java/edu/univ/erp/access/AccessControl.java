// Enforces role-based permissions for Admin, Instructor, and Student actions
package edu.univ.erp.access;

import edu.univ.erp.auth.SessionManager;
import java.util.*;

public class AccessControl {

    private static final Map<String, Set<String>> PERMISSIONS = new HashMap<>();

    static {
        PERMISSIONS.put("ADMIN", new HashSet<>(Arrays.asList(
                "register",
                "drop",
                "toggleMaintenance",
                "createUser",
                "createCourse",
                "createSection",
                "assignInstructor",
                "viewAllGrades",
                "viewAllStudents",
                "disableUser",
                "enableUser")));

        PERMISSIONS.put("INSTRUCTOR", new HashSet<>(Arrays.asList(
                "gradeStudent",
                "viewSectionsTeaching",
                "viewStudentsInSection")));

        PERMISSIONS.put("STUDENT", new HashSet<>(Arrays.asList(
                "register",
                "drop",
                "viewOwnGrades",
                "viewOwnSchedule",
                "viewCatalog")));
    }

    public static boolean isAllowedForCurrentUser(String permission) {
        if (!SessionManager.isLoggedIn()) {
            System.out.println("No user logged in.");
            return false;
        }

        String role = SessionManager.getCurrentRole();
        if (role == null)
            return false;

        Set<String> allowed = PERMISSIONS.get(role.toUpperCase());
        if (allowed == null)
            return false;

        boolean ok = allowed.contains(permission);
        if (!ok) {
            System.out.println("Role '" + role + "' does NOT have permission: " + permission);
        }

        return ok;
    }

    public static boolean isAllowed(String role, String permission) {
        if (role == null)
            return false;
        Set<String> allowed = PERMISSIONS.get(role.toUpperCase());
        return allowed != null && allowed.contains(permission);
    }

    public static Set<String> getPermissionsForCurrentUser() {
        if (!SessionManager.isLoggedIn())
            return Collections.emptySet();
        return getPermissions(SessionManager.getCurrentRole());
    }

    public static Set<String> getPermissions(String role) {
        if (role == null)
            return Collections.emptySet();
        return PERMISSIONS.getOrDefault(role.toUpperCase(), Collections.emptySet());
    }
}
