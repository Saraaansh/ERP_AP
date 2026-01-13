// Business logic layer for instructor operations including grading and section management
package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.SettingDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;

import java.util.List;

public class InstructorService {
    private final EnrollmentDAO enrollmentDAO;
    private final SectionDAO sectionDAO;
    private final GradeDAO gradeDAO;
    private final SettingDAO settingsDAO;
    private final edu.univ.erp.data.InstructorDAO instructorDAO;
    private final edu.univ.erp.data.CourseDAO courseDAO;

    public InstructorService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.sectionDAO = new SectionDAO();
        this.gradeDAO = new GradeDAO();
        this.settingsDAO = new SettingDAO();
        this.instructorDAO = new edu.univ.erp.data.InstructorDAO();
        this.courseDAO = new edu.univ.erp.data.CourseDAO();
        ensureTablesExist();
    }

    public InstructorService(EnrollmentDAO enrollmentDAO, SectionDAO sectionDAO, GradeDAO gradeDAO,
            SettingDAO settingsDAO, edu.univ.erp.data.InstructorDAO instructorDAO,
            edu.univ.erp.data.CourseDAO courseDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.sectionDAO = sectionDAO;
        this.gradeDAO = gradeDAO;
        this.settingsDAO = settingsDAO;
        this.instructorDAO = instructorDAO;
        this.courseDAO = courseDAO;
        ensureTablesExist();
    }

    private void ensureTablesExist() {
        String sqlAssessments = """
                    CREATE TABLE IF NOT EXISTS assessments (
                        assessment_id INT AUTO_INCREMENT PRIMARY KEY,
                        section_id INT NOT NULL,
                        component_name VARCHAR(100) NOT NULL,
                        weight INT NOT NULL CHECK (weight > 0 AND weight <= 100),
                        FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
                        UNIQUE KEY unique_section_component (section_id, component_name)
                    );
                """;
        String sqlTerms = """
                    CREATE TABLE IF NOT EXISTS terms (
                        term_id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) UNIQUE NOT NULL,
                        start_date DATE NOT NULL,
                        end_date DATE NOT NULL,
                        drop_deadline DATE NOT NULL
                    );
                """;
        try (java.sql.Connection conn = edu.univ.erp.data.DBconnection.getConnection();
                java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sqlAssessments);
            stmt.execute(sqlTerms);
        } catch (Exception e) {
            System.err.println("Failed to ensure tables exist: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public edu.univ.erp.domain.Instructor getInstructor(int userId) {
        return instructorDAO.getById(userId);
    }

    public edu.univ.erp.domain.Course getCourse(int courseId) {
        return courseDAO.getById(courseId);
    }

    public java.util.List<edu.univ.erp.domain.Grade> getGradesForEnrollment(int enrollmentId) {
        return gradeDAO.getByEnrollment(enrollmentId);
    }

    public java.util.List<edu.univ.erp.domain.Assessment> getAssessments(int sectionId) {
        return assessmentDAO.getBySection(sectionId);
    }

    private void requireInstructorPermission(String action) throws Exception {
        if (!AccessControl.isAllowedForCurrentUser(action)) {
            throw new Exception(" Only INSTRUCTOR can perform this operation.");
        }
    }

    private void verifyInstructorOwnsSection(int sectionId) throws Exception {
        int instructorId = SessionManager.getCurrentUserId();

        Section s = sectionDAO.getById(sectionId);
        if (s == null) {
            throw new Exception("Section not found.");
        }

        if (s.getInstructorid() != instructorId) {
            throw new Exception(" You are not the instructor of this section.");
        }
    }

    public List<Section> getSectionsTeaching() throws Exception {
        requireInstructorPermission("viewSectionsTeaching");

        int instructorId = SessionManager.getCurrentUserId();

        return sectionDAO.getByInstructor(instructorId);
    }

    public List<Enrollment> getStudentsInSection(int sectionId) throws Exception {

        requireInstructorPermission("viewStudentsInSection");
        verifyInstructorOwnsSection(sectionId);

        return enrollmentDAO.getBySection(sectionId);
    }

    public boolean addGrade(int sectionId, int studentId, String component, double score) throws Exception {

        requireInstructorPermission("gradeStudent");

        if (settingsDAO.isMaintenanceOn()) {
            throw new Exception("🚧 System under maintenance. Please try later.");
        }

        verifyInstructorOwnsSection(sectionId);

        if (studentId <= 0) {
            throw new Exception(" Invalid student ID.");
        }

        if (component == null || component.isBlank()) {
            throw new Exception(" Grade component cannot be empty.");
        }

        if (score < 0 || score > 100) {
            throw new Exception(" Score must be between 0 and 100.");
        }

        if (!enrollmentDAO.exists(studentId, sectionId)) {
            throw new Exception(" Student is not enrolled in this section.");
        }

        List<Enrollment> enrolls = enrollmentDAO.getByStudent(studentId);
        Enrollment match = null;
        for (Enrollment e : enrolls) {
            if (e.getSectionid() == sectionId && e.getStatus().equalsIgnoreCase("active")) {
                match = e;
                break;
            }
        }

        if (match == null) {
            throw new Exception(" Active enrollment not found.");
        }

        Grade g = new Grade();
        g.setEnrollmentId(match.getEnrollmentid());
        g.setComponent(component);
        g.setScore(score);

        return gradeDAO.insert(g);
    }

    public boolean addGrade(int enrollmentId, String component, double score) throws Exception {
        requireInstructorPermission("gradeStudent");

        if (settingsDAO.isMaintenanceOn()) {
            throw new Exception("🚧 System under maintenance. Please try later.");
        }

        if (component == null || component.isBlank()) {
            throw new Exception(" Grade component cannot be empty.");
        }

        if (score < 0 || score > 100) {
            throw new Exception(" Score must be between 0 and 100.");
        }

        Enrollment e = enrollmentDAO.getById(enrollmentId);
        if (e == null) {
            throw new Exception("Enrollment not found.");
        }

        verifyInstructorOwnsSection(e.getSectionid());

        Grade g = new Grade();
        g.setEnrollmentId(enrollmentId);
        g.setComponent(component);
        g.setScore(score);

        return gradeDAO.insert(g);
    }

    public boolean updateGrade(int gradeId, String component, double score, String finalGrade) throws Exception {

        requireInstructorPermission("gradeStudent");

        if (settingsDAO.isMaintenanceOn()) {
            throw new Exception("🚧 System under maintenance. Please try later.");
        }

        if (gradeId <= 0) {
            throw new Exception(" Invalid grade ID.");
        }

        if (component == null || component.isBlank()) {
            throw new Exception(" Grade component cannot be empty.");
        }

        if (score < 0 || score > 100) {
            throw new Exception(" Score must be between 0 and 100.");
        }

        Grade g = gradeDAO.getById(gradeId);
        if (g == null) {
            throw new Exception("Grade record not found.");
        }

        Enrollment e = enrollmentDAO.getById(g.getEnrollmentid());

        if (e == null) {
            throw new Exception("Enrollment not found for grade.");
        }

        verifyInstructorOwnsSection(e.getSectionid());

        g.setComponent(component);
        g.setScore(score);
        g.setFinalgrade(finalGrade);

        return gradeDAO.update(g);
    }

    public List<Grade> getGradesForSection(int sectionId) throws Exception {

        requireInstructorPermission("viewStudentsInSection");
        verifyInstructorOwnsSection(sectionId);

        return gradeDAO.getBySection(sectionId);
    }

    private final edu.univ.erp.data.AssessmentDAO assessmentDAO = new edu.univ.erp.data.AssessmentDAO();

    public boolean createAssessment(int sectionId, String name, int weight) throws Exception {
        System.out.println(
                "DEBUG: createAssessment called with sectionId=" + sectionId + ", name=" + name + ", weight=" + weight);
        if (assessmentDAO == null)
            System.out.println("DEBUG: assessmentDAO is NULL");
        if (sectionDAO == null)
            System.out.println("DEBUG: sectionDAO is NULL");

        requireInstructorPermission("gradeStudent");
        verifyInstructorOwnsSection(sectionId);

        if (weight <= 0 || weight > 100)
            throw new Exception("Weight must be 1-100.");
        if (name == null || name.isBlank())
            throw new Exception("Name cannot be empty.");

        List<edu.univ.erp.domain.Assessment> existing = assessmentDAO.getBySection(sectionId);
        int total = existing.stream().mapToInt(edu.univ.erp.domain.Assessment::getWeight).sum();
        if (total + weight > 100) {
            throw new Exception("Total weight cannot exceed 100%. Current: " + total + "%");
        }

        edu.univ.erp.domain.Assessment a = new edu.univ.erp.domain.Assessment(0, sectionId, name, weight);
        return assessmentDAO.insert(a);
    }

    public boolean deleteAssessment(int assessmentId) throws Exception {
        requireInstructorPermission("gradeStudent");
        // Ideally verify ownership, but assessment doesn't have instructorId directly.
        // We can get assessment -> section -> instructor.
        edu.univ.erp.domain.Assessment a = assessmentDAO.getById(assessmentId);
        if (a == null)
            return false;

        verifyInstructorOwnsSection(a.getSectionId());

        return assessmentDAO.delete(assessmentId);
    }

    public boolean computeFinalGrade(int enrollmentId) throws Exception {
        if (!AccessControl.isAllowedForCurrentUser("gradeStudent")) {
            throw new Exception("Only INSTRUCTOR can compute grades.");
        }

        Enrollment en = enrollmentDAO.getById(enrollmentId);
        if (en == null)
            throw new Exception("Enrollment not found.");

        List<edu.univ.erp.domain.Assessment> assessments = assessmentDAO.getBySection(en.getSectionid());
        if (assessments.isEmpty())
            throw new Exception("No assessments defined for this section.");

        int totalWeight = assessments.stream().mapToInt(edu.univ.erp.domain.Assessment::getWeight).sum();
        if (totalWeight != 100) {
            throw new Exception("Assessment weights must sum to 100% (Current: " + totalWeight + "%).");
        }

        List<Grade> studentGrades = gradeDAO.getByEnrollment(enrollmentId);

        double numeric = 0;
        for (edu.univ.erp.domain.Assessment a : assessments) {

            Grade match = studentGrades.stream()
                    .filter(g -> g.getComponent().equalsIgnoreCase(a.getComponentName()))
                    .findFirst()
                    .orElse(null);

            double score = (match != null) ? match.getScore() : 0.0;
            numeric += (score * a.getWeight()) / 100.0;
        }

        String letter;
        if (numeric >= 85)
            letter = "A";
        else if (numeric >= 70)
            letter = "B";
        else if (numeric >= 55)
            letter = "C";
        else if (numeric >= 40)
            letter = "D";
        else
            letter = "E";

        Grade finalGradeRecord = null;
        for (Grade g : studentGrades) {
            if ("FINAL".equalsIgnoreCase(g.getComponent()) || "FINAL_GRADE".equalsIgnoreCase(g.getComponent())) {
                finalGradeRecord = g;
                break;
            }
        }

        if (finalGradeRecord == null) {

            Grade fg = new Grade();
            fg.setEnrollmentId(enrollmentId);
            fg.setComponent("FINAL_GRADE");
            fg.setScore(numeric);
            fg.setFinalgrade(letter);
            boolean inserted = gradeDAO.insert(fg);
            if (inserted) {
                enrollmentDAO.updateStatus(enrollmentId, "completed");
            }
            return inserted;
        } else {
            finalGradeRecord.setScore(numeric);
            finalGradeRecord.setFinalgrade(letter);
            boolean updated = gradeDAO.update(finalGradeRecord);
            if (updated) {
                enrollmentDAO.updateStatus(enrollmentId, "completed");
            }
            return updated;
        }
    }

    public String exportGradesCsv(int sectionId, String outFilePath) throws Exception {
        if (!AccessControl.isAllowedForCurrentUser("viewStudentsInSection") &&
                !AccessControl.isAllowedForCurrentUser("viewAllGrades")) {
            throw new Exception("Not allowed to export grades.");
        }

        List<Grade> grades = gradeDAO.getBySection(sectionId);
        if (grades.isEmpty())
            throw new Exception("No grades to export for this section.");

        boolean ok = edu.univ.erp.util.CSVExporter.exportObjects(outFilePath, grades);
        if (!ok)
            throw new Exception("CSV export failed.");

        return outFilePath;
    }

}
