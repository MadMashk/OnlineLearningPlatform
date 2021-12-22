package org.example.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.model.Course;

import java.util.List;
import java.util.Objects;

@Schema(description = "учитель")
public class TeacherDTO {
    @Schema(description = "имя", example = "Иван Иванов")
    private String name;
    @Schema(description = "имя пользователя", example = "Учитель1")
    private String userName;
    @Schema(description = "электронная почта", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String email;
    @Schema(description = "список курсов, где преподает учитиль")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeacherDTO that = (TeacherDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(userName, that.userName) && Objects.equals(email, that.email) && Objects.equals(courseList, that.courseList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, userName, email, courseList);
    }
}
