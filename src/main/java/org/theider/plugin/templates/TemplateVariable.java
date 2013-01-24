package org.theider.plugin.templates;

/**
 *
 * @author Tim
 */
public class TemplateVariable {

    private String name;
    private String value;

    public TemplateVariable() {
    }

    public TemplateVariable(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScriptVariable{" + "name=" + name + ", value=" + value + '}';
    }    
    
}
