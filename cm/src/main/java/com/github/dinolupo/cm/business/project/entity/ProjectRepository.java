package com.github.dinolupo.cm.business.project.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called projectRepository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByOwnerContaining(@Param("query") String query, Pageable page);

}
