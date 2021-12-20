package org.example.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "course",
uniqueConstraints = {
@UniqueConstraint(columnNames = "name"),
})
public class Course {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequencecourse", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @Column (name = "name", nullable = false)
    private String name;
    @Column (name="subject", nullable = false)
    private String subject;
    @Column (name = "price")
    private Integer price;
    @Column (name = "descripton")
    private String description;
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="teachers_of_courses",
            joinColumns = {@JoinColumn(name="course_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="teachers_id", referencedColumnName="id")}
    )
    private List <Teacher> teachersOfCourse;
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="attachments_of_courses",
            joinColumns = {@JoinColumn(name="course_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="attachment_id", referencedColumnName="id")}
    )
    private List <Attachment> attachmentsOfCourse;
    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="tasks_of_courses",
            joinColumns = {@JoinColumn(name="course_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name="task_id", referencedColumnName="id")}
    )
    private List <Task> tasksOfCourse;

    public Course() {

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Teacher> getTeachersOfCourse() {
        return teachersOfCourse;
    }

    public void setTeachersOfCourse(List<Teacher> teachersOfCourse) {
        this.teachersOfCourse = teachersOfCourse;
    }

    public List<Attachment> getAttachmentsOfCourse() {
        return attachmentsOfCourse;
    }

    public void setAttachmentsOfCourse(List<Attachment> attachmentsOfCourse) {
        this.attachmentsOfCourse = attachmentsOfCourse;
    }

    public List<Task> getTasksOfCourse() {
        return tasksOfCourse;
    }

    public void setTasksOfCourse(List<Task> tasksOfCourse) {
        this.tasksOfCourse = tasksOfCourse;
    }

}
