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
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.DeleteSnapshotsOption;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * This class for performing delete blob operation.
 */
public class BlobEraser extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME).toString();
        Object fileName = messageContext.getProperty(AzureConstants.FILE_NAME).toString();

        if (containerName == null || fileName == null) {
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }
        String status = AzureConstants.ERR_UNKNOWN_ERROR_OCCURRED;
        try {
            String storageConnectionString = AzureUtil.getStorageConnectionString(messageContext);
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            CloudBlobContainer container = serviceClient.getContainerReference((String) containerName);
            if (container.exists()) {
                CloudBlockBlob blob = container.getBlockBlobReference((String) fileName);
                if (blob.exists()) {
                    blob.delete(DeleteSnapshotsOption.NONE, null, null, null);
                    status = AzureConstants.STATUS_SUCCESS;
                } else {
                    status = AzureConstants.ERR_BLOB_DOES_NOT_EXIST;
                }
            } else {
                status = AzureConstants.ERR_CONTAINER_DOES_NOT_EXIST;
            }
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
     * Generate the result.
     *
     * @param messageContext The message context that is processed by a handler in the handle method.
     * @param status   Result of the status.
     */
    private void generateResults(MessageContext messageContext, String status) {
        String response = AzureUtil.generateResultPayload(AzureConstants.STATUS_SUCCESS.equals(status), status);
        OMElement element = null;
        try {
            element = ResultPayloadCreator.performSearchMessages(response);
        } catch (XMLStreamException e) {
            handleException("Unable to build the message.", e, messageContext);
        }
        ResultPayloadCreator.preparePayload(messageContext, element);
    }
}
