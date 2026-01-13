// Domain model representing a student grade record
package edu.univ.erp.domain;

public class Grade {
    private int gradeid;
    private int enrollmentid;
    private String component;
    private double score;
    private String finalgrade;

    public Grade(){

    }
    public Grade(int gradeid,int enrollmentid,String component, double score, String finalgrade){
        this.gradeid = gradeid;
        this.enrollmentid = enrollmentid;
        this .component = component;
        this.score = score;
        this.finalgrade = finalgrade;

    }
    public int getGradeid() {
        return gradeid;
    }

    public void setGradeId(int gradeid) {
        this.gradeid = gradeid;
    }

    public int getEnrollmentid() {
        return enrollmentid;
    }

    public void setEnrollmentId(int enrollmentid) {
        this.enrollmentid = enrollmentid;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getFinalgrade() {
        return finalgrade;
    }

    public void setFinalgrade(String finalgrade) {
        this.finalgrade = finalgrade;
    }
    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeid +
                ", enrollmentId=" + enrollmentid +
                ", component='" + component + '\'' +
                ", score=" + score +
                ", finalGrade='" + finalgrade + '\'' +
                '}';
    }
}
