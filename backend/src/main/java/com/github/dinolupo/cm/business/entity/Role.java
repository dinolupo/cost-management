package com.github.dinolupo.cm.business.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "role")
@Entity
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_role"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role role = (Role) o;

        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return 1179619963;
    }
}