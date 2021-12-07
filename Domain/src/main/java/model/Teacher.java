package model;

import javax.persistence.*;
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @SequenceGenerator(name="generator", sequenceName = "sequenceofteachers", schema = "senla", allocationSize = 1)
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
}
