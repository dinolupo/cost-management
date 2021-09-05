package com.github.dinolupo.cm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

//@AllArgsConstructor
//@Getter
public class ErrorInfo {
    String timestamp;
    int status;
    String error;
    String trace;
    String message;
    String path;

    public ErrorInfo(String timestamp, int status, String error, String trace, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.trace = trace;
        this.message = message;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getTrace() {
        return trace;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
