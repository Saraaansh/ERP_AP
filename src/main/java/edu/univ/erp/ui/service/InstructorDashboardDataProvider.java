package edu.univ.erp.ui.service;

import java.util.List;

public interface InstructorDashboardDataProvider {

    InstructorSummary loadSummary();

    List<SectionRow> loadSections();

    List<AssessmentRow> loadAssessments(String sectionId);

    List<StudentRow> loadStudentsInSection(String sectionId);

    List<StatRow> loadSectionStats(String sectionId);

    // -------- inner types --------

    class InstructorSummary {
        private final String instructorName;
        private final int sectionsThisTerm;
        private final int totalStudents;
        private final int pendingGrading;
        private final String termLabel;

        public InstructorSummary(String instructorName,
                                 int sectionsThisTerm,
                                 int totalStudents,
                                 int pendingGrading,
                                 String termLabel) {
            this.instructorName = instructorName;
            this.sectionsThisTerm = sectionsThisTerm;
            this.totalStudents = totalStudents;
            this.pendingGrading = pendingGrading;
            this.termLabel = termLabel;
        }

        public String getInstructorName() { return instructorName; }
        public int getSectionsThisTerm() { return sectionsThisTerm; }
        public int getTotalStudents() { return totalStudents; }
        public int getPendingGrading() { return pendingGrading; }
        public String getTermLabel() { return termLabel; }
    }

    class SectionRow {
        private final String sectionId;
        private final String courseCode;
        private final String term;

        public SectionRow(String sectionId, String courseCode, String term) {
            this.sectionId = sectionId;
            this.courseCode = courseCode;
            this.term = term;
        }

        public String getSectionId() { return sectionId; }
        public String getCourseCode() { return courseCode; }
        public String getTerm() { return term; }
    }

    class AssessmentRow {
        private final String name;
        private final int weight;

        public AssessmentRow(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() { return name; }
        public int getWeight() { return weight; }
    }

    class StudentRow {
        private final String studentId;
        private final String name;

        public StudentRow(String studentId, String name) {
            this.studentId = studentId;
            this.name = name;
        }

        public String getStudentId() { return studentId; }
        public String getName() { return name; }
    }

    class StatRow {
        private final String label;
        private final String value;

        public StatRow(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() { return label; }
        public String getValue() { return value; }
    }
}
