package org.example.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @SequenceGenerator(name="generator", sequenceName = "sequenceteacher", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="generator")
    private Integer id;
    @Column (name = "name")
    private String name;
    @Column (name ="username")
    private String userName;

    public Teacher(){

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(id, teacher.id) && Objects.equals(name, teacher.name) && Objects.equals(userName, teacher.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userName);
    }
}
