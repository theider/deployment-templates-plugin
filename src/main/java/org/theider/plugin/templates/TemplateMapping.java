package org.theider.plugin.templates;

/**
 *
 * @author Tim
 */
public class TemplateMapping {
    
    private String templateFilename;
    private String destinationFilename;
    private boolean executable;
    private Boolean parseTemplate;

    public Boolean getParseTemplate() {
        return parseTemplate;
    }

    public void setParseTemplate(Boolean parseTemplate) {
        this.parseTemplate = parseTemplate;
    }    

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public String getTemplateFilename() {
        return templateFilename;
    }

    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public String getDestinationFilename() {
        return destinationFilename;
    }

    public void setDestinationFilename(String destinationFilename) {
        this.destinationFilename = destinationFilename;
    }

    @Override
    public String toString() {
        return "TemplateMapping{" + "templateFilename=" + templateFilename + ", destinationFilename=" + destinationFilename + ", executable=" + executable + ", parseTemplate=" + parseTemplate + '}';
    }
    
}
