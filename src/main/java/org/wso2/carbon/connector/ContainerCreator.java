/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
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
package org.wso2.carbon.connector;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.util.AzureConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreator;

import javax.xml.stream.XMLStreamException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * This class for performing create container operation.
 */
public class ContainerCreator extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        String accountName = messageContext.getProperty(AzureConstants.ACCOUNT_NAME).toString();
        String accountKey = messageContext.getProperty(AzureConstants.ACCOUNT_KEY).toString();
        String containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME).toString();

        boolean resultStatus = false;
        String storageConnectionString = AzureConstants.ENDPOINT_PARAM + accountName + AzureConstants.SEMICOLON
                + AzureConstants.ACCOUNT_KEY_PARAM + accountKey;
        CloudStorageAccount account;
        CloudBlobClient blobClient;
        CloudBlobContainer container;
        try {
            account = CloudStorageAccount.parse(storageConnectionString);
            blobClient = account.createCloudBlobClient();
            container = blobClient.getContainerReference(containerName);
            resultStatus = container.createIfNotExists();
        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        } catch (StorageException e) {
            handleException("Error occurred while connecting to the storage.", e, messageContext);
        }
        ResultPayloadCreator resultPayload = new ResultPayloadCreator();
        generateResults(messageContext, resultStatus, resultPayload);
    }

    /**
     * Generate the result.
     *
     * @param messageContext The message context that is processed by a handler in the handle method.
     * @param resultStatus   Result of the status (true/false).
     */
    private void generateResults(MessageContext messageContext, boolean resultStatus,
                                 ResultPayloadCreator resultPayload) {
        String response = AzureConstants.START_TAG + resultStatus + AzureConstants.END_TAG;
        OMElement element = null;
        try {
            element = resultPayload.performSearchMessages(response);
        } catch (XMLStreamException e) {
            handleException("Unable to build the message.", e, messageContext);
        }
        resultPayload.preparePayload(messageContext, element);
    }
}
