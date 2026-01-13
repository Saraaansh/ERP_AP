// Domain model representing a course entity
package edu.univ.erp.domain;

public class Course {
    private int courseid;
    private String code;
    private String title;
    private int credits;
    public Course(){

    }

    public Course(int courseid , String code , String title , int credits){
        this.courseid = courseid;
        this.code =code;
        this.title =title;
        this.credits =credits;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseid +
                ", code='" + code + '\'' +
                ", title='" + title + '\'' +
                ", credits=" + credits +
                '}';
    }
}
