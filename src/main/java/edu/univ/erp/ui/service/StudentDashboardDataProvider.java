package edu.univ.erp.ui.service;

import java.util.List;

public interface StudentDashboardDataProvider {

    // Main method that dashboard calls
    StudentDashboardSnapshot loadSnapshot();

    // Snapshot of everything the dashboard needs
    class StudentDashboardSnapshot {
        private final String studentName;
        private final int currentRegistrations;
        private final int completedCourses;
        private final int termCredits;
        private final double cgpa;
        private final int completedCredits;
        private final int totalProgramCredits;
        private final boolean maintenanceOn;
        private final List<TodayClassRow> todayClasses;
        private final List<String> alerts;

        public StudentDashboardSnapshot(
                String studentName,
                int currentRegistrations,
                int completedCourses,
                int termCredits,
                double cgpa,
                int completedCredits,
                int totalProgramCredits,
                boolean maintenanceOn,
                List<TodayClassRow> todayClasses,
                List<String> alerts
        ) {
            this.studentName = studentName;
            this.currentRegistrations = currentRegistrations;
            this.completedCourses = completedCourses;
            this.termCredits = termCredits;
            this.cgpa = cgpa;
            this.completedCredits = completedCredits;
            this.totalProgramCredits = totalProgramCredits;
            this.maintenanceOn = maintenanceOn;
            this.todayClasses = todayClasses;
            this.alerts = alerts;
        }

        public String getStudentName() { return studentName; }
        public int getCurrentRegistrations() { return currentRegistrations; }
        public int getCompletedCourses() { return completedCourses; }
        public int getTermCredits() { return termCredits; }
        public double getCgpa() { return cgpa; }
        public int getCompletedCredits() { return completedCredits; }
        public int getTotalProgramCredits() { return totalProgramCredits; }
        public boolean isMaintenanceOn() { return maintenanceOn; }
        public List<TodayClassRow> getTodayClasses() { return todayClasses; }
        public List<String> getAlerts() { return alerts; }

        public int getProgramPercent() {
            if (totalProgramCredits <= 0) return 0;
            int pct = (int) Math.round((completedCredits * 100.0) / totalProgramCredits);
            if (pct < 0) pct = 0;
            if (pct > 100) pct = 100;
            return pct;
        }
    }

    // One row for "Today’s classes" table
    class TodayClassRow {
        private final String timeRange;
        private final String courseCode;
        private final String courseTitle;
        private final String sectionCode;
        private final String room;

        public TodayClassRow(String timeRange,
                             String courseCode,
                             String courseTitle,
                             String sectionCode,
                             String room) {
            this.timeRange = timeRange;
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.sectionCode = sectionCode;
            this.room = room;
        }

        public String getTimeRange() { return timeRange; }
        public String getCourseCode() { return courseCode; }
        public String getCourseTitle() { return courseTitle; }
        public String getSectionCode() { return sectionCode; }
        public String getRoom() { return room; }
    }
}
