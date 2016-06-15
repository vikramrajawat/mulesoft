/**
 * MuleSoft Examples
 * Copyright 2014 MuleSoft, Inc.
 *
 * This product includes software developed at
 * MuleSoft, Inc. (http://www.mulesoft.com/).
 */

package org.mule.examples;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.NullPayload;

public class WebServiceConsumerIT extends FunctionalTestCase
{
	private static String MESSAGE;
	
	@Rule
	public DynamicPort port = new DynamicPort("http.port");
		
    @Override
    protected String getConfigResources()
    {
        return "tshirt-service-consumer.xml";
    }

    @BeforeClass
	public static void init() {    	
    	try {
			MESSAGE = FileUtils.readFileToString(new File("./src/test/resources/message.json"));
			MESSAGE = MESSAGE.replace("ID", String.valueOf(new Date().getTime() / 1000));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private static final String MAPPINGS_FOLDER_PATH = "./mappings";

	@Override
	protected Properties getStartUpProperties() {
		Properties properties = new Properties(super.getStartUpProperties());

		String pathToResource = MAPPINGS_FOLDER_PATH;
		File graphFile = new File(pathToResource);

		properties.put(MuleProperties.APP_HOME_DIRECTORY_PROPERTY,
				graphFile.getAbsolutePath());

		return properties;
	}
	
    @Test
    public void testWebServiceConsumer() throws Exception
    {
        MuleClient client = new MuleClient(muleContext);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("http.method", "POST");
        props.put("Content-Type", "application/json");
        MuleMessage result = client.send("http://localhost:" + port.getNumber() + "/orders", MESSAGE, props);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        assertTrue(result.getPayloadAsString().contains("orderId"));
        
        result = client.send("http://localhost:" + port.getNumber() + "/inventory", null, props);
        assertNotNull(result);
        assertFalse(result.getPayload() instanceof NullPayload);
        assertTrue(result.getPayloadAsString().contains("\"productCode\": \"1412\""));
        assertTrue(result.getPayloadAsString().contains("\"description\": \"Prueba2\""));
        assertTrue(result.getPayloadAsString().contains("\"count\": \"5\""));
        
    }

}
