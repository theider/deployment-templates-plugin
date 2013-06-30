package org.theider.plugin.templates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.logging.Log;

/**
 * @author Tim
 */
public class TemplateParser {

    protected enum ParseState {
        parseText,
        parseVarStart,
        parseVarName,
    }
    
    private final String namespace;

    public TemplateParser(String namespace) {
        this.namespace = namespace;
    }
        
    public String parseTemplate(Log log, String sourceText,List<TemplateVariable> vars) throws TemplateParseException {
        final char[] source = sourceText.toCharArray();
        Map<String,TemplateVariable> varMap = new HashMap<String,TemplateVariable>();
        for(TemplateVariable v : vars) {
            if(varMap.containsKey(v.getName())) {
                throw new TemplateParseException("duplicate template variable:" + v.getName());
            }
            varMap.put(v.getName(),v);
        }
        log.debug("parse template:" + sourceText);
        log.debug("variables=[" + vars + "]");
        ParseState state = ParseState.parseText;
        String resultText = "";
        // go through the script finding {$varname} and replace it with variable values        
        String varName = "";
        for(int i=0;i < source.length;i++) {
            char c = source[i];
            switch(state) {
                case parseVarName:
                    if(c=='}') {
                        // find variable
                        Integer addend = null;
                        if(varName.startsWith(namespace)) {
                            if(varName.contains("+")) {
                                String[] v = varName.split("\\+");
                                varName = v[0].trim();
                                addend = Integer.parseInt(v[1]);                                
                            }
                            TemplateVariable v = varMap.get(varName);
                            if(v == null) {
                                throw new TemplateParseException("undefined variable:" + varName);
                            }
                            String s = v.getValue();
                            if(addend != null) {
                                Integer x = Integer.parseInt(s);
                                x += addend;
                                s = Integer.toString(x);
                            }
                            resultText += s;                            
                        } else {
                            resultText += ("${" + varName + "}");
                        }
                        varName = "";
                        state = ParseState.parseText;                        
                    } else {
                        varName += c;
                    }
                    break;
                case parseVarStart:
                    if(c == '{') {
                        state = ParseState.parseVarName;
                        varName = "";
                    } else {
                        resultText += '$';
                        resultText += c;
                        state = ParseState.parseText;
                    }
                    break;
                case parseText:
                    if(c == '$') {
                        state = ParseState.parseVarStart;
                    } else {
                        resultText += c;
                    }
                    break;                    
            }
        }                
        return resultText;
    }
    
}
