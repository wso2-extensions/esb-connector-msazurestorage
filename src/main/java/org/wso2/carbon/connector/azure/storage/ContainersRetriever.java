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

import com.azure.storage.blob.BlobServiceClient;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.azure.storage.connection.AzureStorageConnectionHandler;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.connection.ConnectionHandler;

/**
 * This class for performing list containers operation.
 */
public class ContainersRetriever extends AbstractConnector {

    public void connect(MessageContext messageContext) {

        ConnectionHandler handler = ConnectionHandler.getConnectionHandler();
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        ResultPayloadCreator.preparePayload(messageContext, result);
        try {
            String connectionName = AzureUtil.getConnectionName(messageContext);
            AzureStorageConnectionHandler azureStorageConnectionHandler = (AzureStorageConnectionHandler)
                    handler.getConnection(AzureConstants.CONNECTOR_NAME, connectionName);
            BlobServiceClient blobServiceClient = azureStorageConnectionHandler.getBlobServiceClient();
            blobServiceClient.listBlobContainers().forEach(blobContainerItem -> {
                OMElement containerElement = factory.createOMElement(AzureConstants.CONTAINER, ns);
                containerElement.setText(blobContainerItem.getName());
                result.addChild(containerElement);
            });
            messageContext.getEnvelope().getBody().addChild(result);
        } catch (Exception e) {
            handleException("Error occurred: " + e.getMessage(), messageContext);
        }
        messageContext.getEnvelope().getBody().addChild(result);
    }
}
