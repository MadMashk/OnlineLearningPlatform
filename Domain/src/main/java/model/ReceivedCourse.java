package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import model.constants.CompleteStatus;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "receivedcourse")
public class ReceivedCourse {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequenceofreceivedcourses", schema = "senla", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @JoinColumn(name="course",referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    private Course course;
    @JoinColumn(name="student",referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
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
}
