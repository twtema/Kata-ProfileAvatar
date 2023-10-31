package org.kata.exception;

public class ImageIsCorruptedException extends RuntimeException {
    public ImageIsCorruptedException(String message) {
        super(message);
    }

}
