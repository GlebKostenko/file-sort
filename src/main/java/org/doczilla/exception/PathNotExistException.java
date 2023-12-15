package org.doczilla.exception;

import java.io.IOException;

public class PathNotExistException extends IOException {
    public PathNotExistException(String message) {
        super("File doesn't exist " + message);
    }
}
