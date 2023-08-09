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

import com.azure.core.http.rest.Response;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.DeleteSnapshotsOptionType;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.azure.storage.connection.AzureStorageConnectionHandler;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.connection.ConnectionHandler;

import javax.xml.stream.XMLStreamException;

/**
 * This class for performing delete blob operation.
 */
public class BlobEraser extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME);
        Object fileName = messageContext.getProperty(AzureConstants.FILE_NAME);

        if (containerName == null || fileName == null) {
            handleException("Mandatory parameters [containerName] and [fileName] cannot be empty.", messageContext);
        }
        ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
        String status = AzureConstants.ERR_UNKNOWN_ERROR_OCCURRED;
        try {
            String connectionName = AzureUtil.getConnectionName(messageContext);
            AzureStorageConnectionHandler azureStorageConnectionHandler = (AzureStorageConnectionHandler)
                    handler.getConnection(AzureConstants.CONNECTOR_NAME, connectionName);
            BlobServiceClient blobServiceClient = azureStorageConnectionHandler.getBlobServiceClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName.toString());
            if (containerClient.exists()) {
                BlobClient blobClient = containerClient.getBlobClient(fileName.toString());
                Response<Boolean> response = blobClient.deleteIfExistsWithResponse(DeleteSnapshotsOptionType.INCLUDE, null,
                null,
                null);
                if (response.getStatusCode() == 404) {
                    status = AzureConstants.ERR_BLOB_DOES_NOT_EXIST;
                } else {
                    status = AzureConstants.STATUS_SUCCESS;
                }
            } else {
                status = AzureConstants.ERR_CONTAINER_DOES_NOT_EXIST;
            }
        } catch (Exception e) {
            handleException("Error occurred: " + e.getMessage(), messageContext);
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
