// Domain model representing a student enrollment in a section
package edu.univ.erp.domain;

public class Enrollment {
    private int enrollmentid ;
    private int studentid ;
    private String status;
    private int sectionid;

    public Enrollment(){

    }
    public Enrollment(int enrollmentid , int studentid,int sectionid , String status){
        this.enrollmentid =enrollmentid;
        this.sectionid =sectionid;
        this.studentid=studentid;
        this.status =status;
    }
    public int getEnrollmentid() {
        return enrollmentid;
    }

    public void setEnrollmentid(int enrollmentid) {
        this.enrollmentid = enrollmentid;
    }

    public int getStudentid() {
        return studentid;
    }

    public void setStudentid(int studentid) {
        this.studentid = studentid;
    }

    public int getSectionid() {
        return sectionid;
    }

    public void setSectionid(int sectionid) {
        this.sectionid = sectionid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentid +
                ", studentId=" + studentid +
                ", sectionId=" + sectionid +
                ", status='" + status + '\'' +
                '}';
    }
}
