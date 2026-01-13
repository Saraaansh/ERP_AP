// Domain model representing an instructor entity
package edu.univ.erp.domain;

public class Instructor {
    private int userid;
    private String department;
    private String name;
    private String email;

    public Instructor() {

    }

    public Instructor(int userid, String department, String name, String email) {
        this.userid = userid;
        this.department = department;
        this.name = name;
        this.email = email;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override

    public String toString() {
        return "Instructor{" +
                "id=" + userid +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
