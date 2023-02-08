/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.azure.storage.util;

import com.sun.crypto.provider.DESCipher;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.connector.core.ConnectException;

/**
 * This class contain required util methods for to Azure Storage connector.
 */
public class AzureUtil {
    public static String getStorageConnectionString(MessageContext messageContext) throws ConnectException {
        Object accountName = messageContext.getProperty(AzureConstants.ACCOUNT_NAME);
        Object accountKey = messageContext.getProperty(AzureConstants.ACCOUNT_KEY);
        if (accountKey == null || accountName == null ){
            throw new ConnectException("Missing authentication parameters.");
        }
        String protocol = AzureConstants.DEFAULT_PROTOCOL;
        Object protocolObject = messageContext.getProperty(AzureConstants.PROTOCOL);
        if (protocolObject != null) {
            protocol = protocolObject.toString();
        }
        return AzureConstants.PROTOCOL_KEY_PARAM + protocol + AzureConstants.SEMICOLON +
               AzureConstants.ACCOUNT_NAME_PARAM + accountName + AzureConstants.SEMICOLON
               + AzureConstants.ACCOUNT_KEY_PARAM + accountKey;
    }

    public static String generateResultPayload(boolean status, String description) {
        if (status) {
            return AzureConstants.START_TAG + true + AzureConstants.END_TAG;
        }
        return AzureConstants.START_TAG_ERROR + description + AzureConstants.END_TAG_ERROR;
    }
}
