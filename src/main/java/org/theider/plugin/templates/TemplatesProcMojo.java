package org.theider.plugin.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * @author Tim
 * @goal templates
 */
public class TemplatesProcMojo extends AbstractMojo {

    /**
     * @parameter expression="${descriptorFile}" default-value="deployment-descriptor.xml"
     */
    private String descriptorFile;
    
    /**
     * @parameter expression="${configurationFile}" default-value="deployment-configuration.properties"
     */
    private String configurationFile;
    
    /**
     * @parameter expression="${templateNamespace}"
     */    
    private String templateNamespace;
    
    protected void copyFile(File sourceFile,File destinationFile) throws IOException {
        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            inStream = new FileInputStream(sourceFile);
            outStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[8192];

            int length;
            //copy the file content in bytes 
            while ((length = inStream.read(buffer)) > 0){
                outStream.write(buffer, 0, length);
            }
        } finally {
            if(inStream != null) {
                inStream.close();
            }
            if(outStream != null) {
                outStream.close();
            }
        }
    }
    
    /**
     * Move templates into the installation from the defining XML     
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        if(templateNamespace == null) {
            throw new MojoFailureException("templateNamespace must be defined.");
        }
        try {
            log.info("Deployment template processor starting.");
            log.info("configuration file:" + configurationFile);
            File propFile = new File(configurationFile);
            Properties props = new Properties();
            props.load(new FileInputStream(propFile));
            log.info("----------------------------------------");
            log.info("deployment properties");
            log.info("----------------------------------------");
            for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();) {
                String name = (String) it.next();
                log.info("property " + name + "=" + props.getProperty(name));            
            }
            // get templates            
            log.info("deployment descriptor:" + descriptorFile);
            Deployment deployment = DeploymentSaxHandler.getDeployment(new FileInputStream(new File(descriptorFile)));
            log.info("loaded deployment:" + deployment);
            // add target folders
            log.info("creating target folders:" + deployment.getFolderNames().toString());
            for(String folderName : deployment.getFolderNames()) {
                File f = new File(folderName);
                if(!f.exists()) {
                    f.mkdir();
                    log.info("--> created new folder:" + f.getAbsolutePath());
                }
            }            
            // process templates
            processTemplates(deployment,props);
            // add files
            // add target folders
            log.info("moving environment files");
            for(FileMapping fmap : deployment.getFileMappings()) {
                log.info("environmnt file:" + fmap.toString());
                // dest is full path
                File destinationFile = new File(fmap.getDestinationFilename());
                String domainName = System.getProperty(templateNamespace + ".domain");
                String environmentName = System.getProperty(templateNamespace + ".environment");
                String sourceName = "environments" + File.separatorChar + domainName + File.separatorChar + environmentName + File.separatorChar + fmap.getSourceFilename();
                File sourceFile = new File(sourceName);
                log.info("file:" + sourceFile + " to " + destinationFile);
                copyFile(sourceFile,destinationFile);
            }                        
            log.info("Finished processing templates.");
        } catch (IOException ex) {
            throw new MojoFailureException("template processing error",ex);
        }
    }

    private void processTemplates(Deployment deployment,Properties props) throws IOException {
        Log log = getLog();
        log.info("processing templates");
        for(TemplateMapping template : deployment.getTemplateMappings()) {
            log.info("process:" + template);
            processTemplate(template,props);
        }
    }

    private void processTemplate(TemplateMapping template,Properties props) throws IOException {
        try {
            Log log = getLog();
            File in = new File(template.getTemplateFilename());
            FileInputStream fin = new FileInputStream(in);
            int L = fin.available();
            byte[] data = new byte[L];
            fin.read(data);
//            log.info("BEFORE");
//            log.info(new String(data));
//            log.info("AFTER");
            TemplateParser parser = new TemplateParser(templateNamespace);
            List<TemplateVariable> vars = new ArrayList<TemplateVariable>();
            for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();) {
                String name = (String) it.next();
                String value = props.getProperty(name);
                TemplateVariable tv = new TemplateVariable(name,value);
                log.debug("var:" + tv);
                vars.add(tv);
            }
            String text = parser.parseTemplate(log,new String(data), vars);            
            //log.info(text);            
            fin.close();
            File outputFile = new File(template.getDestinationFilename());
            FileOutputStream fout = new FileOutputStream(outputFile);
            fout.write(text.getBytes());
            fout.flush();
            fout.close();
            if(template.isExecutable()) {
                log.info("marking output file as executable");
                outputFile.setExecutable(true,false);
            }
        } catch (TemplateParseException ex) {
            throw new IOException("template parse failure",ex);
        }
    }
    
}
