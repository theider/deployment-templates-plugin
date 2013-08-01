package org.theider.plugin.templates;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;

/**
 *
 * @author Timothy Heider
 */
public class NewRelicDeployMarker {

    public void markDeployment(String newRelicApiKey, String deploymentApName,String deploymentUser, String deploymentVersion) throws IOException {        
        Logger log = Logger.getLogger(NewRelicDeployMarker.class);
        URL url = new URL("https://api.newrelic.com/deployments.xml");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");        
        connection.setUseCaches(false);        
        connection.setDoInput(true);
        connection.setDoOutput(true);
        
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        String xmlPostText = String.format("deployment[app_name]=%s;deployment[user]=%s;deployment[revision]=%s",
                                    deploymentApName,
                                    deploymentUser,
                                    deploymentVersion
                                    );
        connection.setRequestProperty("Content-Length", Integer.toString(xmlPostText.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setRequestProperty("x-api-key", newRelicApiKey);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(xmlPostText);
        wr.flush();
        wr.close();

        //Get Response	
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder responseBuf = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            responseBuf.append(line);
            responseBuf.append('\r');
        }
        rd.close();
        String responseText = responseBuf.toString();
        log.debug("==============");
        log.debug("response data:");
        log.debug("--------------");
        log.debug(responseText);
        int responseCode = connection.getResponseCode();
        log.debug("response code:" + responseCode);        
        if(responseCode != 201) {
            throw new IOException("response code:" + responseCode);                    
        }
    }
    
}
