package org.theider.plugin.templates;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Tim
 */
public class DeploymentSaxHandler extends DefaultHandler {

    private Deployment deployment = null;
    
    private TemplateMapping templateMapping;
    
    private FileMapping fileMapping;

    public Deployment getDeployment() {
        return deployment;    
    }
    
    public static Deployment getDeployment(InputStream in) throws IOException {
        try {
            DeploymentSaxHandler handler = new DeploymentSaxHandler();
            // Obtain a new instance of a SAXParserFactory.
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // Specifies that the parser produced by this code will provide support for XML namespaces.
            factory.setNamespaceAware(false);
            // Specifies that the parser produced by this code will validate documents as they are parsed.
            factory.setValidating(false);
            // Creates a new instance of a SAXParser using the currently configured factory parameters.
            SAXParser saxParser = factory.newSAXParser();            
            InputSource ins = new InputSource(in);
            saxParser.parse(ins, handler);
            return handler.getDeployment();
        } catch (ParserConfigurationException ex) {
            throw new IOException("error loading deployment descriptor",ex);
        } catch (SAXException ex) {
            throw new IOException("error loading deployment descriptor",ex);
        }
        
    }
    
    protected enum ParserState {
        DEPLOYMENT,
        BODY,
        TEMPLATE_SOURCE,
        TEMPLATE_BODY,
        PARSE_TEMPLATE,
        FILE_BODY,
        FILE_SOURCE_FILENAME,
        FILE_DESTINATION_FILENAME,        
        FOLDER_BODY,
        DESTINATION_PATH;
    };
    
    private ParserState parserState = ParserState.DEPLOYMENT;
    
    private boolean destFileExecutable;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String data = new String(ch,start,length);
        switch(parserState) {
            case FILE_SOURCE_FILENAME:
                fileMapping.setSourceFilename(data);
                break;
            case FILE_DESTINATION_FILENAME:
                fileMapping.setDestinationFilename(data);
                break;
            case TEMPLATE_SOURCE:
                templateMapping.setTemplateFilename(data);                
                break;
            case PARSE_TEMPLATE:
                String trueOrFalse = data;
                if(trueOrFalse.equalsIgnoreCase("FALSE")) {
                    templateMapping.setParseTemplate(Boolean.FALSE);
                } else {
                    templateMapping.setParseTemplate(Boolean.TRUE);
                }
                break;
            case DESTINATION_PATH:
                templateMapping.setDestinationFilename(data);
                break;
            case FOLDER_BODY:
                deployment.getFolderNames().add(data);
                break;
        }
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch(parserState) {
            case DEPLOYMENT:
                if(!qName.equals("deployment")) {
                    throw new SAXException("expecting root node to be deployment");
                }
                deployment = new Deployment();
                parserState = ParserState.BODY;
                break;
            case BODY:
                if(qName.equals("file")) {
                    fileMapping = new FileMapping();
                    parserState = ParserState.FILE_BODY;                                    
                } else if(qName.equals("template")) {
                    templateMapping = new TemplateMapping();
                    parserState = ParserState.TEMPLATE_BODY;                                    
                } else if(qName.equals("folder")) {
                    parserState = ParserState.FOLDER_BODY;      
                } else {
                    throw new SAXException("expecting folder or template node and got " + qName);
                }
                break;
            case TEMPLATE_BODY:
                if(qName.equals("parse-template")) {
                    // true or false.
                    parserState = ParserState.PARSE_TEMPLATE;
                } else if(qName.equals("template-filename")) {
                    // template source
                    parserState = ParserState.TEMPLATE_SOURCE;
                } else if(qName.equals("destination-filename")) {
                    parserState = ParserState.DESTINATION_PATH;
                    destFileExecutable = false;
                    String execFile = attributes.getValue("executable");
                    if(execFile != null) {
                        destFileExecutable = execFile.equalsIgnoreCase("true");
                    }
                } else {
                    throw new SAXException("expecting template-filename or destination-filename nodes (got " + qName + ")");   
                }                                    
                break;                
            case FILE_BODY:
                if(qName.equals("source-filename")) {                    
                    parserState = ParserState.FILE_SOURCE_FILENAME;
                } else if(qName.equals("destination-filename")) {
                    parserState = ParserState.FILE_DESTINATION_FILENAME;
                } else {
                    throw new SAXException("expecting template-filename or destination-filename nodes (got " + qName + ")");   
                }                                    
                break;                                
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(parserState) {
            case PARSE_TEMPLATE:
                parserState = ParserState.TEMPLATE_BODY;
                break;            
            case TEMPLATE_SOURCE:
                parserState = ParserState.TEMPLATE_BODY;
                break;
            case DESTINATION_PATH:
                parserState = ParserState.TEMPLATE_BODY;
                break;
            case FOLDER_BODY:
                if(qName.equals("folder")) {
                    parserState = ParserState.BODY;
                } else {
                    throw new SAXException("missing end folder tag");
                }
            case TEMPLATE_BODY:
                if(qName.equals("template")) {
                    if(templateMapping.getDestinationFilename() == null) {
                        throw new SAXException("template mapping is missing destination path");
                    }
                    if(templateMapping.getTemplateFilename() == null) {
                        throw new SAXException("template mapping is missing template source");
                    }
                    templateMapping.setExecutable(destFileExecutable);
                    deployment.getTemplateMappings().add(templateMapping);
                    parserState = ParserState.BODY;
                }
                break;                
            case FILE_SOURCE_FILENAME:
                parserState = ParserState.FILE_BODY;
                break;
            case FILE_DESTINATION_FILENAME:
                parserState = ParserState.FILE_BODY;
                break;
            case FILE_BODY:
                if(qName.equals("file")) {
                    if(fileMapping.getDestinationFilename() == null) {
                        throw new SAXException("file mapping is missing destination filename");
                    }
                    if(fileMapping.getDestinationFilename() == null) {
                        throw new SAXException("file mapping is missing source filename");
                    }                    
                    System.out.println("added " + fileMapping.toString());
                    deployment.getFileMappings().add(fileMapping);
                    parserState = ParserState.BODY;
                }
                break;                
        }
    }

}
