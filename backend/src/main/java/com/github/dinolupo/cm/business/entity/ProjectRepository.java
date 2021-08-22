package com.github.dinolupo.cm.business.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

// This will be AUTO IMPLEMENTED by Spring into a Bean called projectRepository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByOwnerContaining(@Param("query") String query, Pageable page);

}
