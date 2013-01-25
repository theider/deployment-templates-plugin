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
        folderNames = new ArrayList<String>();
    }
    
    private final List<TemplateMapping> templateMappings;
    
    private final List<String> folderNames;

    public List<TemplateMapping> getTemplateMappings() {
        return templateMappings;
    }
    
    public List<String> getFolderNames() {
        return folderNames;
    }

    @Override
    public String toString() {
        return "Deployment{" + "templateMappings=" + templateMappings + ", folderNames=" + folderNames + '}';
    }
        
}
