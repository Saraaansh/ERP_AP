// Business logic layer for student operations including registration and grade viewing
package edu.univ.erp.service;

import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.SettingDAO;
import edu.univ.erp.data.TermDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Term;
import edu.univ.erp.access.AccessControl;

import java.time.LocalDate;
import java.util.List;

public class StudentService {
    private final EnrollmentDAO enrollmentDAO;
    private final SectionDAO sectionDAO;
    private final SettingDAO settingsDAO;
    private final edu.univ.erp.data.StudentDAO studentDAO;
    private final edu.univ.erp.data.CourseDAO courseDAO;
    private final edu.univ.erp.data.GradeDAO gradeDAO;
    private final TermDAO termDAO;

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.sectionDAO = new SectionDAO();
        this.settingsDAO = new SettingDAO();
        this.studentDAO = new edu.univ.erp.data.StudentDAO();
        this.courseDAO = new edu.univ.erp.data.CourseDAO();
        this.gradeDAO = new edu.univ.erp.data.GradeDAO();
        this.termDAO = new TermDAO();
    }

    public StudentService(EnrollmentDAO enrollmentDAO, SectionDAO sectionDAO, SettingDAO settingsDAO,
            edu.univ.erp.data.StudentDAO studentDAO, edu.univ.erp.data.CourseDAO courseDAO,
            edu.univ.erp.data.GradeDAO gradeDAO, TermDAO termDAO) {
        this.enrollmentDAO = enrollmentDAO;
        this.sectionDAO = sectionDAO;
        this.settingsDAO = settingsDAO;
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.gradeDAO = gradeDAO;
        this.termDAO = termDAO;
    }

    public edu.univ.erp.domain.Student getStudent(int userId) {
        return studentDAO.getById(userId);
    }

    public List<Enrollment> getEnrollments(int studentId) {
        return enrollmentDAO.getByStudent(studentId);
    }

    public Section getSection(int sectionId) {
        return sectionDAO.getById(sectionId);
    }

    public edu.univ.erp.domain.Course getCourse(int courseId) {
        return courseDAO.getById(courseId);
    }

    public java.util.List<edu.univ.erp.domain.Grade> getGrades(int enrollmentId) {
        return gradeDAO.getByEnrollment(enrollmentId);
    }

    public boolean registerSection(int studentId, int sectionId) throws Exception {

        if (!AccessControl.isAllowedForCurrentUser("register")) {
            throw new Exception("Not allowed to register sections.");
        }

        if (settingsDAO.isMaintenanceOn()) {
            throw new Exception("System in maintenance mode. Try again later.");
        }

        if (studentId <= 0) {
            throw new Exception(" Invalid student ID.");
        }

        if (sectionId <= 0) {
            throw new Exception(" Invalid section ID.");
        }

        Section section = sectionDAO.getById(sectionId);
        if (section == null) {
            throw new Exception("Section not found.");
        }

        int enrolled = sectionDAO.countEnrolled(sectionId);
        if (enrolled >= section.getCapacity()) {
            throw new Exception(" Section is full.");
        }

        // Check Registration Deadline
        if (!isRegistrationAllowed(section)) {
            throw new Exception("Registration is closed for this term.");
        }

        Enrollment existing = enrollmentDAO.getByStudentAndSection(studentId, sectionId);

        if (existing != null) {
            if ("active".equalsIgnoreCase(existing.getStatus())) {
                throw new Exception(" Already enrolled in this section.");
            } else {

                boolean reactivated = enrollmentDAO.updateStatus(existing.getEnrollmentid(), "active");
                if (!reactivated) {
                    throw new Exception("Failed to re-enroll in dropped section.");
                }
                return true;
            }
        }

        Enrollment e = new Enrollment(0, studentId, sectionId, "active");

        boolean success = enrollmentDAO.insert(e);

        if (!success) {
            throw new Exception("Failed to register student into section.");
        }

        return true;
    }

    public boolean dropSection(int studentId, int sectionId) throws Exception {

        if (!AccessControl.isAllowedForCurrentUser("drop")) {
            throw new Exception("Not allowed to drop sections.");
        }

        if (settingsDAO.isMaintenanceOn()) {
            throw new Exception("System in maintenance mode. Try again later.");
        }

        if (studentId <= 0) {
            throw new Exception(" Invalid student ID.");
        }

        if (sectionId <= 0) {
            throw new Exception(" Invalid section ID.");
        }

        Section section = sectionDAO.getById(sectionId);
        if (section == null) {
            throw new Exception("Section not found.");
        }

        if (!isDropAllowed(section)) {
            throw new Exception("Drop deadline passed for this section.");
        }

        if (!enrollmentDAO.exists(studentId, sectionId)) {
            throw new Exception("You are not enrolled in this section.");
        }

        boolean dropped = enrollmentDAO.drop(studentId, sectionId);

        if (!dropped) {
            throw new Exception("Failed to drop the course.");
        }

        return true;
    }

    public boolean isDropAllowed(Section section) {
        if (section == null)
            return false;
        String termName = section.getSemester() + " " + section.getYear();
        Term term = termDAO.getByName(termName);

        // If term not found, assume allowed or handle as error.
        // For safety, let's assume allowed if no term data to avoid blocking.
        if (term == null)
            return true;

        return !LocalDate.now().isAfter(term.getDropDeadline());
    }

    private boolean isRegistrationAllowed(Section section) {
        if (section == null)
            return false;
        String termName = section.getSemester() + " " + section.getYear();
        Term term = termDAO.getByName(termName);

        if (term == null)
            return true; // Fallback

        LocalDate now = LocalDate.now();
        return !now.isBefore(term.getStartDate()) && !now.isAfter(term.getEndDate());
    }

    public boolean exportTranscriptPdf(int studentId, String filePath) {
        return edu.univ.erp.ui.util.PDFService.generateTranscriptForStudent(
                filePath,
                studentId,
                studentDAO,
                enrollmentDAO,
                gradeDAO,
                courseDAO,
                new edu.univ.erp.data.SectionDAO());
    }

    public boolean exportTranscriptCsv(int studentId, String filePath) {
        return edu.univ.erp.ui.util.CSVService.generateTranscriptForStudent(
                filePath,
                studentId,
                studentDAO,
                enrollmentDAO,
                gradeDAO,
                courseDAO,
                new edu.univ.erp.data.SectionDAO());
    }
}
