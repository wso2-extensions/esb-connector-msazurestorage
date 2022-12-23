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
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.json.JSONObject;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This class for getting the metadata from the blob
 */
public class ListMetadata extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME);
        String fileName = messageContext.getProperty(AzureConstants.FILE_NAME).toString();
        if (containerName == null || fileName == null) {
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }
        String outputResult;

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        ResultPayloadCreator.preparePayload(messageContext, result);
        OMElement metadataElement = factory.createOMElement(AzureConstants.METADATA, ns);
        result.addChild(metadataElement);
        try {
            String storageConnectionString = AzureUtil.getStorageConnectionString(messageContext);
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            CloudBlobContainer container = serviceClient.getContainerReference((String) containerName);
            CloudBlob blob = container.getBlobReferenceFromServer(fileName);
            blob.downloadAttributes();
            HashMap<String, String> blobMetadata  = blob.getMetadata();

            for (Map.Entry<String, String> entry : blobMetadata.entrySet()) {
                outputResult = entry.getValue();
                OMElement messageElement = factory.createOMElement(entry.getKey(), ns);
                messageElement.setText(outputResult);
                metadataElement.addChild(messageElement);
            }

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
            handleException("Unexpected error occurred. ", e, messageContext);
        }
        messageContext.getEnvelope().getBody().addChild(result);
    }
}
