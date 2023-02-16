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
package org.wso2.carbon.connector.azure.storage;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;

import javax.xml.stream.XMLStreamException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * This class for performing delete container operation.
 */
public class ContainerEraser extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME);
        if (containerName == null) {
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }
        boolean status = false;
        try {
            String storageConnectionString = AzureUtil.getStorageConnectionString(messageContext);
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            CloudBlobContainer container = serviceClient.getContainerReference((String) containerName);
            status = container.deleteIfExists();
        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        } catch (StorageException e) {
            handleException("Error occurred while connecting to the storage.", e, messageContext);
        } catch (ConnectException e) {
            handleException("Unexpected error occurred. ", e, messageContext);
        }
        generateResults(messageContext, status);
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param status   Result of the status (true/false)
     */
    private void generateResults(MessageContext messageContext, boolean status) {
        String response = AzureUtil
                .generateResultPayload(status, !status ? AzureConstants.ERR_CONTAINER_DOES_NOT_EXIST : "");
        OMElement element = null;
        try {
            element = ResultPayloadCreator.performSearchMessages(response);
        } catch (XMLStreamException e) {
            handleException("Unable to build the message.", e, messageContext);
        }
        ResultPayloadCreator.preparePayload(messageContext, element);
    }
}
