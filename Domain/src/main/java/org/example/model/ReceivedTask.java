package org.example.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.model.constants.CompleteStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table (name = "receivedtask")
public class ReceivedTask {
    @Id
    @SequenceGenerator(name = "generator", sequenceName = "sequencereceivedtask", schema = "senla",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @JoinColumn(name="task",referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Task task;
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

    public ReceivedTask(){


    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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
        ReceivedTask that = (ReceivedTask) o;
        return Objects.equals(id, that.id) && Objects.equals(task, that.task) && Objects.equals(student, that.student) && completeStatus == that.completeStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, task, student, completeStatus);
    }
}

