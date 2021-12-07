package model.dto;

import model.Course;

import java.util.List;

public class TeacherDTO {
    private String name;
    private String userName;
    private String email;
    private List<Course> courseList;

    public TeacherDTO(String name, String userName, String email, List<Course> courseList) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.courseList = courseList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }
}
