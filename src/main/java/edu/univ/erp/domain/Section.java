// Domain model representing a course section entity
package edu.univ.erp.domain;

public class Section {
    private int sectionid;
    private int courseid;
    private int instructorid;
    private String daytime;
    private String room;
    private int capacity;
    private String semester;
    private int year;

    public Section() {

    }

    public Section(int sectionid, int courseid, int instructorid, String daytime, String room, int capacity,
            String semester, int year) {
        this.sectionid = sectionid;
        this.courseid = courseid;
        this.instructorid = instructorid;
        this.daytime = daytime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    public int getSectionid() {
        return sectionid;
    }

    public void setSectionid(int sectionId) {
        this.sectionid = sectionId;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseId) {
        this.courseid = courseId;
    }

    public int getInstructorid() {
        return instructorid;
    }

    public void setInstructorid(int instructorId) {
        this.instructorid = instructorId;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionId=" + sectionid +
                ", courseId=" + courseid +
                ", instructorId=" + instructorid +
                ", dayTime='" + daytime + '\'' +
                ", room='" + room + '\'' +
                ", capacity=" + capacity +
                ", semester='" + semester + '\'' +
                ", year=" + year +
                '}';
    }
}
