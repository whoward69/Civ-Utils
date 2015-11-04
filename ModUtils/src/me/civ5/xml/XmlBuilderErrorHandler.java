package me.civ5.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class XmlBuilderErrorHandler implements ErrorHandler {
    // Method called by the parser on a warning
    public void warning(SAXParseException exception) {
//        log(Level.DEBUG, exception);
    }
    
    // Method called by the parser on an error
    public void error(SAXParseException exception) {
//        log(Level.INFO, exception);
    }
    
    // Method called by the parser on a fatal error
    public void fatalError(SAXParseException exception) {
//        log(Level.FATAL, exception);
    }
    
//    private void log(Priority level, SAXParseException exception) {
//        // System.err.println(exception.getMessage() + " at line " + exception.getLineNumber() + ", column " + exception.getColumnNumber());
//    }
}
