package com.github.dinolupo.cm.business.entity;

import org.springframework.hateoas.server.core.Relation;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "task_cost")
@Entity
@Relation(collectionRelation = "taskCosts", itemRelation = "taskCost")
public class TaskCost {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_task_cost"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "cost", nullable = false)
    private Double cost;

    @Column(name = "source")
    private String source;

    @Column(name = "description")
    private String description;

    @Column(name = "task_id")
    private Long taskId;
}
