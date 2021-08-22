package com.github.dinolupo.cm.business.boundary.exception;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id) {
        super("Could not find project " + id);
    }

}
