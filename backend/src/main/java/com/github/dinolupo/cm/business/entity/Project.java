package com.github.dinolupo.cm.business.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Table(name = "project")
@Entity
@Relation(collectionRelation = "projects", itemRelation = "project")
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Project {

    // order status
    public enum Status {
        READY, //
        IN_PROGRESS, //
        PAUSED, //
        COMPLETED, //
        CANCELLED, //
        CUSTOM //
    }

//    public Project(String name,
//                   String description,
//                   Double budget,
//                   LocalDate startDate,
//                   LocalDate endDate,
//                   String owner,
//                   String estimation,
//                   Status status,
//                   Boolean archived) {
//        this.name = name;
//        this.description = description;
//        this.budget = budget;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.owner = owner;
//        this.estimation = estimation;
//        this.status = status;
//        this.archived = archived;
//    }

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_project"
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

    @Column(name = "owner")
    private String owner;

    @Column(name = "estimation")
    private String estimation;

    // version attribute name with underscore to make it return into the JSon representation
    @Version
    @Column(name = "version")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long _version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "archived")
    private Boolean archived;

    public Long getVersion() {
        return _version;
    }
    public void setVersion(Long version) { this._version = version; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Project project = (Project) o;

        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return 1545761250;
    }
}