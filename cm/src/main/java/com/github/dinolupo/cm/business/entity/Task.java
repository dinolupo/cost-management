package com.github.dinolupo.cm.business.entity;

import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "task")
@Entity
@Relation(collectionRelation = "tasks", itemRelation = "task")
public class Task {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_task"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "budget")
    private Double budget;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "project_id")
    private Long projectId;

    // version attribute name with underscore to make it return into the JSon representation
    @Version
    @Column(name = "version")
    private Long _version;

    public Long getVersion() {
        return _version;
    }
    public void setVersion(Long version) { this._version = version; }

    public Long getProjectId() { return projectId; }

    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}