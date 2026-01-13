// Domain model representing a student entity
package edu.univ.erp.domain;

public class Student {
    private int userId;
    private String name;
    private String rollNo;
    private String program;
    private int year;

    public Student() {}

    public Student(int userId, String name, String rollNo, String program, int year) {
        this.userId = userId;
        this.name = name;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    public int getUserid() {
        return userId;
    }

    public void setUserid(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRollno() {
        return rollNo;
    }

    public void setRollno(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
