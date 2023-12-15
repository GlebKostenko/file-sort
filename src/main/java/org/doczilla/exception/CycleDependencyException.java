package org.doczilla.exception;

public class CycleDependencyException extends RuntimeException {
    public CycleDependencyException(String filePath) {
        super("Cyclic dependency on" + filePath);
    }
}
