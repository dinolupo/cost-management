package com.github.dinolupo.cm.business.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Table(name = "task")
@Entity
@Relation(collectionRelation = "tasks", itemRelation = "task")
@Getter
@Setter
@ToString
@AllArgsConstructor @NoArgsConstructor
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
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long _version;
    public Long getVersion() {
        return _version;
    }
    public void setVersion(Long version) { this._version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;

        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return 1976597858;
    }
}