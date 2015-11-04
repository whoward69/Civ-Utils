package me.civ5.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;

import me.civ5.exception.XmlException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlBuilder extends SAXBuilder implements EntityResolver {
    public static final String defaultEncoding = "UTF-8";
    public static final String defaultParser = "org.apache.xerces.parsers.SAXParser";
    
	private String dtdRoot = null;  // The root that relative paths will be resolved against

    public static Document parse(byte[] bytes) throws XmlException {
    	return parse(new ByteArrayInputStream(bytes));
    }
    
    public static Document parse(InputStream stream) throws XmlException {
    	if ( stream == null ) return null;
    	
    	try {
			return (new XmlBuilder(defaultParser)).build(stream);
		} catch (IOException e) {
			throw new XmlException("Cannot read xml stream", e);
		} catch (JDOMException e) {
			throw new XmlException("Cannot parse xml stream", e);
		}
    }

	public XmlBuilder() {
		this(defaultParser, true);
	}

	public XmlBuilder(String parserClass) {
		this(parserClass, true);
	}

	public XmlBuilder(boolean validate) {
		this(defaultParser, validate);
	}
	
	public XmlBuilder(String parserClass, boolean validate) {
		super(parserClass, validate);
		_init();
	}

	private void _init() {
		setEntityResolver(this);
		
        // setFeature("http://apache.org/xml/features/validation/dynamic", true); // Nice idea ... if it worked!
        setErrorHandler(new XmlBuilderErrorHandler()); // Custom error handler allows for poorly formed article XML
	}
	
	// Method to set the DTD root
	// Would be nice to do this in the constructors
	// but it gets confused with the parserClass parameter
	public void setDtdRoot(String dtdRoot) {
	    this.dtdRoot = dtdRoot;
	}

	// Some build requests we can't cope with
	// as we can't access needed information for resolving relative paths
	@Override
	public Document build(Reader characterStream) throws IOException, JDOMException {
		throw new RuntimeException("Unsupported method: build(Reader characterStream)");
	}
	
	@Override
	public Document build(Reader characterStream, String systemId) throws IOException, JDOMException {
		throw new RuntimeException("Unsupported method: build(Reader characterStream, String systemId)");
	}
	
	@Override
	public Document build(URL url) throws IOException, JDOMException {
		throw new RuntimeException("Unsupported method: build(URL url)");
	}

	// But most build requests are OK
	@Override
	public Document build(String systemId) throws IOException, JDOMException {
		return build(new File(systemId));
	}

	public Document build(byte[] bytes) throws IOException, JDOMException {
		return build(new ByteArrayInputStream(bytes));
	}

	@Override
	public Document build(File file) throws IOException, JDOMException {
		return build(new FileInputStream(file), file.getParent());
	}

	@Override
	public Document build(InputStream in) throws IOException, JDOMException {
		return build(in, null);
	}
	
	@Override
	public Document build(InputStream in, String systemId) throws IOException, JDOMException {
		Document doc = super.build(in);
		// NASTY HACK!
		// Xerces seems to resolve DTD's relative to the execution directory and not relative to the XML file
		// While we can work around this with an EntityResolver for the SAXBuilder class
		// when we pass the resulting JDOM Document into the XSLTransformer it all goes wrong again
		// To work around this we clear the DocType here (after all, if it failed to parse
		// we just failed to load it, so there really is no need for the XSLTransformer to parse it again)
		doc.setDocType(null);  
		
		return doc;
	}

	
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		URL urlEntity = new URL(systemId);
		if ( publicId != null || dtdRoot == null || !urlEntity.getProtocol().equalsIgnoreCase("file")) {
			return null; // Use the default behaviour
		}

		File partialEntity = new File(URLDecoder.decode(urlEntity.getPath(), defaultEncoding));
		File fullEntity = new File(dtdRoot, partialEntity.getName());

		if ( fullEntity.exists() ) {
			return new InputSource(new FileInputStream(fullEntity));
		} else {
			return new InputSource(new ByteArrayInputStream("".getBytes("UTF-16"))); 
		}
	}
}
