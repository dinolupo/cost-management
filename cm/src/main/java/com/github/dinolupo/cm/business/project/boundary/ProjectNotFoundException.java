package com.github.dinolupo.cm.business.project.boundary;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id) {
        super("Could not find project " + id);
    }

}
