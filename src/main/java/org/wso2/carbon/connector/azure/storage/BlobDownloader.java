/*
 *  Copyright (c) 2022, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.carbon.connector.azure.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.transport.TransportUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.transport.passthru.PassThroughConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.NoSuchElementException;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

/**
 * This class for performing download blobs operation.
 */
public class BlobDownloader extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME);
        String fileName = messageContext.getProperty(AzureConstants.FILE_NAME).toString();

        if (containerName == null || fileName == null) {
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        ResultPayloadCreator.preparePayload(messageContext, result);
        try {
	        String storageConnectionString = AzureUtil.getStorageConnectionString(messageContext);
	        CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
	        CloudBlobClient serviceClient = account.createCloudBlobClient();

            CloudBlobContainer container = serviceClient.getContainerReference((String) containerName);
            CloudBlockBlob blob = container.getBlockBlobReference(fileName);
	        
	        
	        FileDataSource fileDataSource = new FileDataSource(fileName);
	        DataHandler handler = new DataHandler(fileDataSource);
	        blob.download(handler.getOutputStream());
	        
	        String contentType = handler.getContentType();
	        org.apache.axis2.context.MessageContext axis2MsgCtx = 
	        		((org.apache.synapse.core.axis2.Axis2MessageContext) messageContext)
	        		.getAxis2MessageContext();
	        Builder builder = BuilderUtil.getBuilderFromSelector(contentType, axis2MsgCtx);
           
                      	
           	JsonUtil.removeJsonPayload(axis2MsgCtx);
            axis2MsgCtx.
                    removeProperty(PassThroughConstants.NO_ENTITY_BODY);
           	OMElement fileElement = builder.processDocument(handler.getInputStream(), contentType, axis2MsgCtx);
           	messageContext.setEnvelope(TransportUtils.createSOAPEnvelope(fileElement));
               result.addChild(factory.createOMElement("someProp", ns));
        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        } catch (StorageException e) {
            handleException("Error occurred while connecting to the storage.", e, messageContext);
        } catch (NoSuchElementException e) {
            // No such element exception can be occurred due to server authentication failure.
            handleException("Error occurred while listing the container", e, messageContext);
        } catch (ConnectException e) {
            handleException("Unexpected error occurred.", e, messageContext);
		} catch (IOException e) {
			handleException("Error while building the response.", e, messageContext);
		}
        messageContext.getEnvelope().getBody().addChild(result);        
    }
}
