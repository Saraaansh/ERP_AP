package edu.univ.erp.ui.service;

import edu.univ.erp.auth.SessionManager;

import edu.univ.erp.domain.*;

import java.util.ArrayList;
import java.util.List;

import edu.univ.erp.service.MaintenanceService;
import edu.univ.erp.service.StudentService;

public class RealStudentDashboardProvider implements StudentDashboardDataProvider {
    private final StudentService studentService = new StudentService();
    private final MaintenanceService maintenanceService = new MaintenanceService();

    @Override
    public StudentDashboardSnapshot loadSnapshot() {

        Student stu = SessionManager.getCurrentStudent();
        if (stu == null) {
            // Fallback: Try to load using currentUserId
            int userId = SessionManager.getCurrentUserId();
            if (userId != -1) {
                System.out.println("DEBUG: Session student is null, reloading for ID: " + userId);
                stu = studentService.getStudent(userId);
                if (stu != null) {
                    SessionManager.setCurrentStudent(stu);
                }
            }
        }

        if (stu == null) {
            return new StudentDashboardSnapshot(
                    "Unknown", 0, 0, 0, 0, 0, 120, false,
                    new ArrayList<>(),
                    List.of("Student profile not loaded."));
        }

        int studentId = stu.getUserid();
        List<Enrollment> enrolls = studentService.getEnrollments(studentId);

        int activeRegs = 0;
        int completedCourses = 0;
        int completedCredits = 0;

        List<TodayClassRow> today = new ArrayList<>();

        for (Enrollment en : enrolls) {
            System.out.println("DEBUG: Processing enrollment " + en.getEnrollmentid() + " status=" + en.getStatus());

            if ("active".equalsIgnoreCase(en.getStatus())) {
                activeRegs++;
            } else if ("completed".equalsIgnoreCase(en.getStatus())) {
                // Count completed but don't show in Today's Classes
                Section sec = studentService.getSection(en.getSectionid());
                if (sec != null) {
                    Course course = studentService.getCourse(sec.getCourseid());
                    if (course != null) {
                        completedCourses++;
                        completedCredits += course.getCredits();
                    }
                }
                continue;
            } else {
                // Dropped or other status
                continue;
            }

            // If we are here, it's an active enrollment
            Section sec = studentService.getSection(en.getSectionid());
            if (sec == null)
                continue;

            Course course = studentService.getCourse(sec.getCourseid());
            if (course == null)
                continue;

            // Build today's classes
            if (sec.getDaytime() != null) {
                boolean isToday = false;
                String dt = sec.getDaytime();

                switch (java.time.LocalDate.now().getDayOfWeek()) {
                    case MONDAY -> isToday = dt.contains("M");
                    case TUESDAY -> isToday = dt.contains("T") && !dt.equals("Th") && !dt.startsWith("Th");
                    case WEDNESDAY -> isToday = dt.contains("W");
                    case THURSDAY -> isToday = dt.contains("Th");
                    case FRIDAY -> isToday = dt.contains("F");
                    case SATURDAY -> isToday = dt.contains("S");
                    case SUNDAY -> isToday = false;
                }

                if (java.time.LocalDate.now().getDayOfWeek() == java.time.DayOfWeek.TUESDAY) {
                    java.util.regex.Pattern p = java.util.regex.Pattern.compile("T(?!h)");
                    java.util.regex.Matcher m = p.matcher(dt);
                    isToday = m.find();
                }

                if (isToday) {
                    today.add(new TodayClassRow(
                            sec.getDaytime(),
                            course.getCode(),
                            course.getTitle(),
                            "SEC-" + sec.getSectionid(),
                            sec.getRoom()));
                }
            }
        }

        boolean maintenanceOn = maintenanceService.isMaintenanceActive();

        double cgpa = computeCgpa(studentId);

        return new StudentDashboardSnapshot(
                stu.getName(),
                activeRegs,
                completedCourses,
                activeRegs * 3, // placeholder
                cgpa,
                completedCredits,
                120,
                maintenanceOn,
                today,
                List.of("Welcome " + stu.getName()));
    }

    private double computeCgpa(int studentId) {
        List<Enrollment> list = studentService.getEnrollments(studentId);
        double totalPoints = 0;
        double totalCredits = 0;

        for (Enrollment en : list) {
            // Check if there is a final grade, regardless of enrollment status
            List<Grade> grades = studentService.getGrades(en.getEnrollmentid());
            if (grades.isEmpty())
                continue;

            String finalGrade = null;
            for (Grade g : grades) {
                if ("FINAL_GRADE".equalsIgnoreCase(g.getComponent())) {
                    finalGrade = g.getFinalgrade();
                    break;
                }
            }

            if (finalGrade == null)
                continue;

            Section sec = studentService.getSection(en.getSectionid());
            if (sec == null)
                continue;

            Course course = studentService.getCourse(sec.getCourseid());
            if (course == null)
                continue;

            double gp = gradeToPoints(finalGrade);

            totalPoints += gp * course.getCredits();
            totalCredits += course.getCredits();
        }

        if (totalCredits == 0)
            return 0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    private double gradeToPoints(String g) {
        if (g == null)
            return 0;
        return switch (g.toUpperCase()) {
            case "A" -> 10;
            case "B" -> 8;
            case "C" -> 6;
            case "D" -> 5;
            case "E" -> 4;
            default -> 0;
        };
    }

}
