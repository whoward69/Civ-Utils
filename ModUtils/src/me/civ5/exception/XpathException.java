package me.civ5.exception;

public class XpathException extends ModException {
    public XpathException() {
        super();
    }

    public XpathException(String message) {
        super(message);
    }

    public XpathException(Throwable cause) {
        this("Unspecified XpathException", cause);
    }

    public XpathException(String message, Throwable cause) {
        super(message, cause);
    }
}
