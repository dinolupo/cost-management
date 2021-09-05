package com.github.dinolupo.cm.exception;

public class ElementNotFoundException extends RuntimeException {

    public ElementNotFoundException(Class<?> clazz, Long id) {
        super(String.format("Could not find %s %d ", clazz, id));
    }

    public  ElementNotFoundException(Class<?> clazzParent, Long idParent, Class<?> clazz, Long id) {
        super(String.format("Could not find %s %d for %s %d",
                clazz.getSimpleName(), id,
                clazzParent.getSimpleName(), idParent)
        );
    }

    public ElementNotFoundException(Class<?> clazz, String description) {
        super(String.format("Could not find %s: %s", description));
    }
}
