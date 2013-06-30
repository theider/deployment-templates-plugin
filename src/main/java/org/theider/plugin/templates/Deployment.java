package org.theider.plugin.templates;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tim
 */
public class Deployment {

    public Deployment() {
        templateMappings = new ArrayList<TemplateMapping>();
        fileMappings = new ArrayList<FileMapping>();
        folderNames = new ArrayList<String>();
    }
    
    private final List<TemplateMapping> templateMappings;
    
    private final List<FileMapping> fileMappings;
    
    private final List<String> folderNames;

    public List<TemplateMapping> getTemplateMappings() {
        return templateMappings;
    }
    
    public List<String> getFolderNames() {
        return folderNames;
    }

    public List<FileMapping> getFileMappings() {
        return fileMappings;
    }

    @Override
    public String toString() {
        return "Deployment{" + "templateMappings=" + templateMappings + ", fileMappings=" + fileMappings + ", folderNames=" + folderNames + '}';
    }
    
}
