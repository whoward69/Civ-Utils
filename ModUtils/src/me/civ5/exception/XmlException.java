package me.civ5.exception;

public class XmlException extends ModException {
    public XmlException() {
        super();
    }

    public XmlException(String message) {
        super(message);
    }

    public XmlException(Throwable cause) {
        this("Unspecified XmlException", cause);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
