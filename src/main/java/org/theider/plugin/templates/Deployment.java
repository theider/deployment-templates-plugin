package org.theider.plugin.templates;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tim
 */
public class Deployment {
    
    private List<TemplateMapping> templateMappings = new ArrayList<TemplateMapping>();

    public List<TemplateMapping> getTemplateMappings() {
        return templateMappings;
    }

    public void setTemplateMappings(List<TemplateMapping> templateMappings) {
        this.templateMappings = templateMappings;
    }

    @Override
    public String toString() {
        return "Deployment{" + "templateMappings=" + templateMappings + '}';
    }
        
}
