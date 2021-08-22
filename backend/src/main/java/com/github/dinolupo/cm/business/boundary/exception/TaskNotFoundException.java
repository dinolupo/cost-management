package com.github.dinolupo.cm.business.boundary.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long projectId, Long id) {
        super("Could not find task " + id + " on project " + projectId);
    }
}
