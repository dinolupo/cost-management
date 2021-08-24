package com.github.dinolupo.cm.business.boundary.exception;

import com.github.dinolupo.cm.business.entity.Role;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(Class<?> clazz, Long id) {
        super(String.format("Could not find %s %l ", clazz, id));
    }

    public  ElementNotFoundException(Class<?> clazzParent, Long idParent, Class<?> clazz, Long id) {
        super(String.format("Could not find %s %l for %s %l",
                clazz.getSimpleName(), id,
                clazzParent.getSimpleName(), idParent)
        );
    }

    public ElementNotFoundException(Class<?> clazz, String description) {
        super(String.format("Could not find %s: %s", description));
    }
}
