package org.example.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table (name = "task")
public class Task {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "generator", sequenceName = "sequencetask", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = " description")
    private String description;
    @Column(name = "pointsforcompletion")
    private Integer pointsForCompletion;

    public Task() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPointsForCompletion() {
        return pointsForCompletion;
    }

    public void setPointsForCompletion(Integer pointsForCompletion) {
        this.pointsForCompletion = pointsForCompletion;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(pointsForCompletion, task.pointsForCompletion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, pointsForCompletion);
    }
}

