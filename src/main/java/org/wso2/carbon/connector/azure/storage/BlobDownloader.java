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

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.wso2.carbon.connector.azure.storage.connection.AzureStorageConnectionHandler;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.azure.storage.util.Error;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.connection.ConnectionHandler;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.stream.XMLStreamException;

/**
 * This class for performing download blobs operation.
 */
public class BlobDownloader extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        Object containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME);
        Object fileName = messageContext.getProperty(AzureConstants.FILE_NAME);

        if (containerName == null || fileName == null) {
            AzureUtil.setErrorPropertiesToMessage(messageContext, new Error(AzureConstants.BAD_REQUEST, "Mandatory " +
                    "parameters [containerName] and [fileName] cannot be empty."));
            handleException("Mandatory parameters [containerName] and [fileName] cannot be empty.", messageContext);
        }
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        ResultPayloadCreator.preparePayload(messageContext, result);
        org.apache.axis2.context.MessageContext axis2MessageContext =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();
        ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
        try {
            String connectionName = AzureUtil.getConnectionName(messageContext);
            AzureStorageConnectionHandler azureStorageConnectionHandler = (AzureStorageConnectionHandler)
                    handler.getConnection(AzureConstants.CONNECTOR_NAME, connectionName);
            BlobServiceClient blobServiceClient = azureStorageConnectionHandler.getBlobServiceClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName.toString());
            if (containerClient.exists()) {
                BlobClient blobClient = containerClient.getBlobClient(fileName.toString());
                if (blobClient.exists()) {
                    FileDataSource fileDataSource = new FileDataSource(fileName.toString());
                    DataHandler dataHandler = new DataHandler(fileDataSource);
                    blobClient.downloadStream(dataHandler.getOutputStream());
                    String contentType = blobClient.getProperties().getContentType();
                    Builder builder = BuilderUtil.getBuilderFromSelector(contentType, axis2MessageContext);

                    JsonUtil.removeJsonPayload(axis2MessageContext);
                    OMElement fileElement = builder.processDocument(dataHandler.getInputStream(), contentType,
                            axis2MessageContext);
                    messageContext.setEnvelope(TransportUtils.createSOAPEnvelope(fileElement));
                    if (StringUtils.isNotEmpty(contentType)) {
                        axis2MessageContext.setProperty("ContentType", contentType);
                    }
                } else {
                    generateResults(messageContext, AzureConstants.ERR_BLOB_DOES_NOT_EXIST);
                }
            } else {
                generateResults(messageContext, AzureConstants.ERR_CONTAINER_DOES_NOT_EXIST);
            }
        } catch (Exception e) {
            AzureUtil.setErrorPropertiesToMessage(messageContext, new Error(AzureConstants.INTERNAL_SERVER_ERROR,
                    e.getMessage()));
            handleException("Error occurred: " + e.getMessage(), messageContext);
        }
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param status   Result of the status (true/false)
     */
    private void generateResults(MessageContext messageContext, String status) {
        String response = AzureUtil.generateResultPayload(false, status);
        OMElement element = null;
        try {
            element = ResultPayloadCreator.performSearchMessages(response);
        } catch (XMLStreamException e) {
            handleException("Unable to build the message.", e, messageContext);
        }
        ResultPayloadCreator.preparePayload(messageContext, element);
    }
}
