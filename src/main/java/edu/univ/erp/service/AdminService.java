// Business logic layer for admin operations including user and course management
package edu.univ.erp.service;

import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.auth.AuthManager;

import java.util.List;
import java.util.stream.Collectors;

public class AdminService {

    private final SettingDAO settingsDAO = new SettingDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AuthManager authManager = new AuthManager();
    private final TermDAO termDAO = new TermDAO();

    public List<Term> getAllTerms() {
        return termDAO.getAll();
    }

    public boolean createTerm(Term t) {
        try {
            return termDAO.insert(t);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTerm(Term t) {
        try {
            return termDAO.update(t);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTerm(int termId) {
        try {
            return termDAO.delete(termId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Course> getAllCourses() {
        return courseDAO.getAll();
    }

    public boolean createCourse(Course c) {
        try {
            if (c.getCredits() <= 0)
                return false;
            return courseDAO.insert(c);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCourse(Course c) {
        try {
            if (c.getCredits() <= 0)
                return false;
            return courseDAO.update(c);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCourse(int courseId) {
        try {
            List<Section> sections = sectionDAO.getAllSections();
            for (Section s : sections) {
                if (s.getCourseid() == courseId) {
                    return false;
                }
            }
            return courseDAO.delete(courseId);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Section> getAllSections() {
        return sectionDAO.getAllSections();
    }

    public boolean createSection(Section s) {
        try {
            if (s.getCapacity() <= 0)
                return false;
            return sectionDAO.insertSection(s);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSection(Section s) {
        try {
            if (s.getCapacity() <= 0)
                return false;
            return sectionDAO.updateSection(s);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        try {
            int enrolled = sectionDAO.countEnrolled(sectionId);
            if (enrolled > 0)
                return false;
            return sectionDAO.deleteSection(sectionId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean assignInstructor(int sectionId, int instructorId) {
        try {
            return sectionDAO.assignInstructor(sectionId, instructorId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createStudentProfile(String username, String password, Student profile) {
        try {
            boolean ok = authManager.createUser(username, "STUDENT", password);
            if (!ok)
                return false;

            User u = userDAO.getByUsername(username);
            if (u == null)
                return false;

            profile.setUserid(u.getUserId());
            return studentDAO.insert(profile);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createInstructorProfile(String username, String password, Instructor profile) {
        try {
            boolean ok = authManager.createUser(username, "INSTRUCTOR", password);
            if (!ok)
                return false;

            User u = userDAO.getByUsername(username);
            if (u == null)
                return false;

            profile.setUserid(u.getUserId());
            return instructorDAO.insert(profile);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    public List<User> searchUsers(String roleFilter, String keyword) {
        List<User> list = userDAO.getAll();

        if (roleFilter != null && !"All".equalsIgnoreCase(roleFilter)) {
            list = list.stream()
                    .filter(u -> u.getRole().equalsIgnoreCase(roleFilter))
                    .collect(Collectors.toList());
        }

        if (keyword != null && !keyword.isEmpty()) {
            String key = keyword.toLowerCase();
            list = list.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(key)
                            || String.valueOf(u.getUserId()).contains(key))
                    .collect(Collectors.toList());
        }

        return list;
    }

    public boolean createAdminUser(String username, String password) {
        try {
            return authManager.createUser(username, "ADMIN", password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int userId, String username, String role) {
        try {
            return userDAO.updateUser(userId, username, role);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            User u = userDAO.getById(userId);
            if (u == null)
                return false;

            if ("STUDENT".equalsIgnoreCase(u.getRole()))
                studentDAO.deleteByUserId(userId);

            if ("INSTRUCTOR".equalsIgnoreCase(u.getRole()))
                instructorDAO.deleteByUserId(userId);

            return userDAO.delete(userId);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toggleMaintenance(boolean enabled) {
        try {
            return settingsDAO.setMaintenance(enabled);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMaintenanceOn() {
        return settingsDAO.isMaintenanceOn();
    }
}
