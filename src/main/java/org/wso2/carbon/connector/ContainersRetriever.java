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

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.util.AzureConstants;
import org.wso2.carbon.connector.util.ResultPayloadCreator;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

/**
 * This class for performing list containers operation.
 */
public class ContainersRetriever extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        String accountName = messageContext.getProperty(AzureConstants.ACCOUNT_NAME).toString();
        String accountKey = messageContext.getProperty(AzureConstants.ACCOUNT_KEY).toString();
        String storageConnectionString = AzureConstants.ENDPOINT_PARAM + accountName + AzureConstants.SEMICOLON
                + AzureConstants.ACCOUNT_KEY_PARAM + accountKey;

        ResultPayloadCreator resultPayload = new ResultPayloadCreator();
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        resultPayload.preparePayload(messageContext, result);
        String outputResult;
        CloudStorageAccount account;
        CloudBlobClient serviceClient;
        try {
            account = CloudStorageAccount.parse(storageConnectionString);
            serviceClient = account.createCloudBlobClient();
            for (CloudBlobContainer container : serviceClient.listContainers()) {
                outputResult = container.getName();
                OMElement messageElement = factory.createOMElement(AzureConstants.CONTAINER, ns);
                messageElement.setText(outputResult);
                result.addChild(messageElement);
            }
        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        }
        messageContext.getEnvelope().getBody().addChild(result);
    }
}
