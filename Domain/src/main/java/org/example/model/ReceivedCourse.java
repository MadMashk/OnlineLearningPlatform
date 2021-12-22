package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.model.constants.CompleteStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "receivedcourse")
public class ReceivedCourse {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequencerecivedcourse", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @JoinColumn(name="course",referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Course course;
    @JoinColumn(name="student",referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Student student;
    @Enumerated(EnumType.STRING)
    @Column(name ="completestatus")
    private CompleteStatus completeStatus;
    @Column(name = "dateofreceiving")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dateOfReceiving;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "dateofending")
    private Date dateOfEnding;

    public  ReceivedCourse(){

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CompleteStatus getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(CompleteStatus completeStatus) {
        this.completeStatus = completeStatus;
    }

    public Date getDateOfReceiving() {
        return dateOfReceiving;
    }

    public void setDateOfReceiving(Date dateOfReceiving) {
        this.dateOfReceiving = dateOfReceiving;
    }

    public Date getDateOfEnding() {
        return dateOfEnding;
    }

    public void setDateOfEnding(Date dateOfEnding) {
        this.dateOfEnding = dateOfEnding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceivedCourse that = (ReceivedCourse) o;
        return Objects.equals(id, that.id) && Objects.equals(course, that.course) && Objects.equals(student, that.student) && completeStatus == that.completeStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, course, student, completeStatus);
    }
}
