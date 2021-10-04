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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

/**
 * This class for performing upload blob operation.
 */
public class BlobUploader extends AbstractConnector {

    public void connect(MessageContext messageContext) {
        if (messageContext.getProperty(AzureConstants.ACCOUNT_NAME) == null || messageContext.getProperty(
                AzureConstants.ACCOUNT_KEY) == null || messageContext.getProperty(AzureConstants.CONTAINER_NAME) == null
                || messageContext.getProperty(AzureConstants.FILE_NAME) == null || messageContext.getProperty
                (AzureConstants.FILE_PATH) == null){
            handleException("Mandatory parameters cannot be empty.", messageContext);
        }

        String containerName = messageContext.getProperty(AzureConstants.CONTAINER_NAME).toString();
        String fileName = messageContext.getProperty(AzureConstants.FILE_NAME).toString();
        String filePath = messageContext.getProperty(AzureConstants.FILE_PATH).toString();

        boolean resultStatus = false;
        String storageConnectionString = AzureUtil.getStorageConnectionString(messageContext);
        FileInputStream fileInputStream = null;
        try {
            CloudStorageAccount account = CloudStorageAccount.parse(storageConnectionString);
            CloudBlobClient serviceClient = account.createCloudBlobClient();
            CloudBlobContainer container = serviceClient.getContainerReference(containerName);
            CloudBlockBlob blob = container.getBlockBlobReference(fileName);
            File sourceFile = new File(filePath);
            fileInputStream = new FileInputStream(sourceFile);
            if (messageContext.getProperty(AzureConstants.BLOB_CONTENT_TYPE) != null) {
                String blobContentType = messageContext.getProperty(AzureConstants.BLOB_CONTENT_TYPE).toString();
                blob.getProperties().setContentType(blobContentType);
            }
            blob.upload(fileInputStream, sourceFile.length());
            resultStatus = true;
        } catch (URISyntaxException e) {
            handleException("Invalid input URL found.", e, messageContext);
        } catch (InvalidKeyException e) {
            handleException("Invalid account key found.", e, messageContext);
        } catch (StorageException e) {
            handleException("Error occurred while connecting to the storage.", e, messageContext);
        } catch (IOException e) {
            handleException("Error occurred while uploading the file.", e, messageContext);
        }
        finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing the file input stream.");
                }
            }
        }
        generateResults(messageContext, resultStatus);
    }

    /**
     * Generate the result
     *
     * @param messageContext The message context that is processed by a handler in the handle method
     * @param resultStatus   Result of the status (true/false)
     */
    private void generateResults(MessageContext messageContext, boolean resultStatus) {
        String response = AzureConstants.START_TAG + resultStatus + AzureConstants.END_TAG;
        OMElement element = null;
        try {
            element = ResultPayloadCreator.performSearchMessages(response);
        } catch (XMLStreamException e) {
            handleException("Unable to build the message.", e, messageContext);
        }
        ResultPayloadCreator.preparePayload(messageContext, element);
    }
}
