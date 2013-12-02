package org.theider.plugin.templates;

/**
 *
 * @author Tim
 */
public class FileMapping {
    
    private String sourceFilename;
    private String destinationFilename;
    
    public String getSourceFilename() {
        return sourceFilename;
    }

    public void setSourceFilename(String sourceFilename) {
        this.sourceFilename = sourceFilename;
    }

    public String getDestinationFilename() {
        return destinationFilename;
    }

    public void setDestinationFilename(String destinationFilename) {
        this.destinationFilename = destinationFilename;
    }

    @Override
    public String toString() {
        return "FileMapping{" + "sourceFilename=" + sourceFilename + ", destinationFilename=" + destinationFilename + '}';
    }
    
}
