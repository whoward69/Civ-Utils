package me.civ5.exception;

public class ModException extends Exception {
    public ModException() {
        super();
    }

    public ModException(String message) {
        super(message);
    }

    public ModException(Throwable cause) {
        this("Unspecified ModException", cause);
    }

    public ModException(String message, Throwable cause) {
        super(message, cause);
    }
}