package edu.univ.erp.ui.util;

import com.opencsv.CSVWriter;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.data.GradeDAO;
import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.EnrollmentDAO;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CSVService {

    public static boolean generateTranscriptForStudent(
            String filePath,
            int studentId,
            StudentDAO studentDAO,
            EnrollmentDAO enrollmentDAO,
            GradeDAO gradeDAO,
            CourseDAO courseDAO,
            SectionDAO sectionDAO) {

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Fetch student
            Student student = studentDAO.getById(studentId);
            if (student == null)
                return false;

            // Header Info
            writer.writeNext(new String[] { "Student Name", student.getName() });
            writer.writeNext(new String[] { "Roll No", student.getRollno() });
            writer.writeNext(new String[] { "Program", student.getProgram() });
            writer.writeNext(new String[] { "Year", String.valueOf(student.getYear()) });
            writer.writeNext(new String[] {}); // Empty line

            // Table Header
            writer.writeNext(
                    new String[] { "Course Code", "Course Title", "Semester", "Year", "Credits", "Final Grade" });

            // Fetch Enrollments
            List<Enrollment> enrollments = enrollmentDAO.getByStudent(studentId);

            for (Enrollment enrollment : enrollments) {
                if (!"active".equalsIgnoreCase(enrollment.getStatus()))
                    continue;

                Section section = sectionDAO.getById(enrollment.getSectionid());
                if (section == null)
                    continue;

                Course course = courseDAO.getById(section.getCourseid());
                if (course == null)
                    continue;

                // Get Final Grade
                List<Grade> grades = gradeDAO.getByEnrollment(enrollment.getEnrollmentid());
                String finalGrade = "N/A";
                for (Grade g : grades) {
                    if (g.getFinalgrade() != null && !g.getFinalgrade().isEmpty()) {
                        finalGrade = g.getFinalgrade();
                        break;
                    }
                }

                writer.writeNext(new String[] {
                        course.getCode(),
                        course.getTitle(),
                        section.getSemester(),
                        String.valueOf(section.getYear()),
                        String.valueOf(course.getCredits()),
                        finalGrade
                });
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
