// Domain model for instructor summary information
package edu.univ.erp.domain;

public class InstructorSummary
{
    private String instructorName;
    private int sectionsThisTerm;
    private int totalStudents;
    private int pendingGrading;
    private String termLabel;

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

