package org.example.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table (name ="student")
public class Student {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "sequencestudent", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String userName;
    @Column(name = "points")
    private long points;

    public Student(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return points == student.points && Objects.equals(id, student.id) && Objects.equals(name, student.name) && Objects.equals(userName, student.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userName, points);
    }
}
