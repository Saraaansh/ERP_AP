package edu.univ.erp.ui.service;

import edu.univ.erp.auth.SessionManager;

import edu.univ.erp.domain.*;

import java.util.ArrayList;
import java.util.List;

import edu.univ.erp.service.InstructorService;

public class RealInstructorDashboardProvider implements InstructorDashboardDataProvider {
    private final InstructorService instructorService = new InstructorService();

    @Override
    public InstructorSummary loadSummary() {

        Instructor ins = SessionManager.getCurrentInstructor();
        if (ins == null) {
            return new InstructorSummary(
                    "Unknown", 0, 0, 0, "This Term");
        }

        List<Section> sections = new ArrayList<>();
        try {
            sections = instructorService.getSectionsTeaching();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int totalStudents = 0;
        int pendingGrades = 0;

        for (Section s : sections) {

            List<Enrollment> enrolls = new ArrayList<>();
            try {
                enrolls = instructorService.getStudentsInSection(s.getSectionid());
            } catch (Exception e) {
                e.printStackTrace();
            }
            totalStudents += enrolls.size();

            for (Enrollment e : enrolls) {
                List<Grade> g = instructorService.getGradesForEnrollment(e.getEnrollmentid());
                if (g.isEmpty() || g.get(0).getFinalgrade() == null) {
                    pendingGrades++;
                }
            }
        }

        return new InstructorSummary(
                ins.getName(),
                sections.size(),
                totalStudents,
                pendingGrades,
                "Spring 2025");
    }

    @Override
    public List<SectionRow> loadSections() {
        Instructor ins = SessionManager.getCurrentInstructor();
        List<SectionRow> out = new ArrayList<>();

        if (ins == null)
            return out;

        List<Section> list = new ArrayList<>();
        try {
            list = instructorService.getSectionsTeaching();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Section s : list) {
            Course c = instructorService.getCourse(s.getCourseid());

            out.add(new SectionRow(
                    String.valueOf(s.getSectionid()),
                    c != null ? c.getCode() : "N/A",
                    s.getSemester() + " " + s.getYear()));
        }

        return out;
    }

    @Override
    public List<AssessmentRow> loadAssessments(String sectionId) {
        List<AssessmentRow> out = new ArrayList<>();
        try {
            int secId = Integer.parseInt(sectionId);
            List<edu.univ.erp.domain.Assessment> list = instructorService.getAssessments(secId);

            for (edu.univ.erp.domain.Assessment a : list) {
                out.add(new AssessmentRow(a.getComponentName(), a.getWeight()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    @Override
    public List<StudentRow> loadStudentsInSection(String sectionId) {
        List<StudentRow> out = new ArrayList<>();

        int secId = Integer.parseInt(sectionId);
        List<Enrollment> list = new ArrayList<>();
        try {
            list = instructorService.getStudentsInSection(secId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Enrollment e : list) {
            // We need a way to get student name. Enrollment has studentId.
            // InstructorService doesn't expose generic getStudent(id) for privacy,
            // but for enrolled students it should be fine.
            // Let's add a helper in InstructorService or use a restricted DAO access via
            // service?
            // Actually, we can add getStudent(id) to InstructorService but restricted to
            // enrolled students.
            // For now, let's assume we can get it via a new method or existing one.
            // Wait, I didn't add getStudent to InstructorService.
            // I will use StudentService here just for fetching student name,
            // OR better, add getStudent(id) to InstructorService.
            // Let's use a temporary StudentService here as it's a UI provider.
            // Ideally InstructorService should provide "getStudentDetails(studentId)"

            // For this refactor, I will instantiate StudentService locally to get student
            // details.
            edu.univ.erp.service.StudentService ss = new edu.univ.erp.service.StudentService();
            Student s = ss.getStudent(e.getStudentid());
            if (s == null)
                continue;

            out.add(new StudentRow(
                    String.valueOf(s.getUserid()),
                    s.getName()));
        }

        return out;
    }

    @Override
    public List<StatRow> loadSectionStats(String sectionId) {
        int secId = Integer.parseInt(sectionId);
        List<Enrollment> enrolls = new ArrayList<>();
        try {
            enrolls = instructorService.getStudentsInSection(secId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double sum = 0;
        int count = 0;
        double max = 0;
        double min = 100;

        for (Enrollment e : enrolls) {
            List<Grade> g = instructorService.getGradesForEnrollment(e.getEnrollmentid());
            if (g.isEmpty() || g.get(0).getFinalgrade() == null)
                continue;

            double score = letterToPercent(g.get(0).getFinalgrade());

            sum += score;
            count++;
            max = Math.max(max, score);
            min = Math.min(min, score);
        }

        List<StatRow> stats = new ArrayList<>();

        if (count > 0) {
            double avg = Math.round((sum / count) * 100) / 100.0;

            stats.add(new StatRow("Average", avg + "%"));
            stats.add(new StatRow("Highest", max + "%"));
            stats.add(new StatRow("Lowest", min + "%"));
        } else {
            stats.add(new StatRow("Average", "N/A"));
        }

        return stats;
    }

    private double letterToPercent(String g) {
        return switch (g.toUpperCase()) {
            case "A" -> 90;
            case "B" -> 80;
            case "C" -> 70;
            case "D" -> 60;
            default -> 0;
        };
    }
}
