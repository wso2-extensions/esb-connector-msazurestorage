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

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.ResultPayloadCreator;
import org.wso2.carbon.connector.core.AbstractConnector;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * This class for performing list containers operation.
 */
public class QueuesPeekMessage extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        if (messageContext.getProperty(AzureConstants.PROTOCOL) == null ||
                messageContext.getProperty(AzureConstants.ACCOUNT_NAME) == null ||
                messageContext.getProperty(AzureConstants.ACCOUNT_KEY) == null) {
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }

        String protocol = messageContext.getProperty(AzureConstants.PROTOCOL).toString();
        String accountName = messageContext.getProperty(AzureConstants.ACCOUNT_NAME).toString();
        String accountKey = messageContext.getProperty(AzureConstants.ACCOUNT_KEY).toString();
        String queueName = messageContext.getProperty(AzureConstants.QUEUE_NAME).toString();

        String storageConnectionString = AzureConstants.PROTOCOL_KEY_PARAM + protocol + AzureConstants.SEMICOLON +
                AzureConstants.ACCOUNT_NAME_PARAM + accountName + AzureConstants.SEMICOLON
                + AzureConstants.ACCOUNT_KEY_PARAM + accountKey + AzureConstants.SEMICOLON;

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMNamespace ns = factory.createOMNamespace(AzureConstants.AZURE_NAMESPACE, AzureConstants.NAMESPACE);
        OMElement result = factory.createOMElement(AzureConstants.RESULT, ns);
        ResultPayloadCreator.preparePayload(messageContext, result);
        String outputResult;
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudQueueClient queueClient = account.createCloudQueueClient();

            // Retrieve a reference to a queue.
            CloudQueue queue = queueClient.getQueueReference(queueName);
            // Peek at the next message.
            CloudQueueMessage peekedMessage = queue.peekMessage();

            // Output the message value.
            if (peekedMessage != null)
            {
                outputResult = peekedMessage.getMessageContentAsString();
                OMElement messageElement = factory.createOMElement(AzureConstants.MESSAGE, ns);
                messageElement.setText(outputResult);
                result.addChild(messageElement);
            }

        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        } catch (Exception e) {
            handleException("Exception: ", e, messageContext);
        }
        messageContext.getEnvelope().getBody().addChild(result);
    }
}
