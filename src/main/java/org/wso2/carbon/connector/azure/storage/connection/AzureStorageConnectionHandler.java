/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.connector.azure.storage.connection;

import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.connector.azure.storage.exceptions.InvalidConfigurationException;
import org.wso2.carbon.connector.azure.storage.util.AzureConstants;
import org.wso2.carbon.connector.azure.storage.util.AzureUtil;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.connection.Connection;

public class AzureStorageConnectionHandler implements Connection {

    private ConnectionConfiguration connectionConfig;
    private BlobServiceClient blobServiceClient;

    public AzureStorageConnectionHandler(ConnectionConfiguration fsConfig) {

        this.connectionConfig = fsConfig;
    }

    /**
     * @return an instance of BlobServiceClient
     */
    public BlobServiceClient getBlobServiceClient() throws ConnectException {

        String clientId = this.connectionConfig.getClientID();
        String clientSecret = this.connectionConfig.getClientSecret();
        String tenantId = this.connectionConfig.getTenantID();
        String accountName = this.connectionConfig.getAccountName();
        String accountKey = this.connectionConfig.getAccountKey();
        String endpointProtocol = this.connectionConfig.getEndpointProtocol();

        if (blobServiceClient == null) {
            if (StringUtils.isNotEmpty(clientId) && StringUtils.isNotEmpty(clientSecret)
                    && StringUtils.isNotEmpty(tenantId) && StringUtils.isNotEmpty(accountName)) {
                ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                        .httpClient(new NettyAsyncHttpClientBuilder().build())
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .tenantId(tenantId)
                        .build();
                blobServiceClient = new BlobServiceClientBuilder()
                        .httpClient(new NettyAsyncHttpClientBuilder().build())
                        .credential(credential)
                        .endpoint(AzureConstants.HTTPS_PROTOCOL + accountName + AzureConstants.BLOB_ENDPOINT_SUFFIX)
                        .buildClient();
            } else if (StringUtils.isNotEmpty(accountName) && StringUtils.isNotEmpty(accountKey)) {
                blobServiceClient = new BlobServiceClientBuilder()
                        .httpClient(new NettyAsyncHttpClientBuilder().build())
                        .connectionString(AzureUtil.getStorageConnectionString(accountName, accountKey, endpointProtocol))
                        .buildClient();
            } else {
                throw new InvalidConfigurationException("Missing authentication parameters.");
            }
        }
        return blobServiceClient;
    }

    public ConnectionConfiguration getConnectionConfig() {

        return connectionConfig;
    }

    public void setConnectionConfig(ConnectionConfiguration connectionConfig) {

        this.connectionConfig = connectionConfig;
    }
}
