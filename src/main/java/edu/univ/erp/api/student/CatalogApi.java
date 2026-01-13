package edu.univ.erp.api.student;

import edu.univ.erp.data.CourseDAO;
import edu.univ.erp.data.EnrollmentDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.data.SectionDAO;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;

import java.util.ArrayList;
import java.util.List;

public class CatalogApi {
    private final SectionDAO sectionDAO = new SectionDAO();
    private final CourseDAO courseDAO = new CourseDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

    public List<SectionRow> listSections() throws Exception {
        List<SectionRow> rows = new ArrayList<>();
        List<Section> sections = sectionDAO.getAllSections();
        for (Section section : sections) {
            Course course = courseDAO.getById(section.getCourseid());
            if (course == null) continue;
            Instructor instructor = instructorDAO.getById(section.getInstructorid());
            String instrName = instructor != null ? instructor.getName() : "TBA";
            int enrolled = sectionDAO.countEnrolled(section.getSectionid());
            String daytime = section.getDaytime() != null ? section.getDaytime() : "TBA";
            String room = section.getRoom() != null ? section.getRoom() : "TBA";
            rows.add(new SectionRow(
                    section.getSectionid(),
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    instrName,
                    daytime,
                    room,
                    enrolled,
                    section.getCapacity()
            ));
        }
        return rows;
    }

    public static record SectionRow(
            int sectionId,
            String courseCode,
            String title,
            int credits,
            String instructor,
            String dayTime,
            String room,
            int enrolled,
            int capacity
    ) {}
}
