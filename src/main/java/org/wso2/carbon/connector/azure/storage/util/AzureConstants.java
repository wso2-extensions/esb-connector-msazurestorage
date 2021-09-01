/*
* Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

/**
 * This class contains the azure connector specific constants.
 */
public class AzureConstants {
    public static final String ACCOUNT_NAME = "azureAccountName";
    public static final String ACCOUNT_KEY = "azureAccountKey";
    public static final String PROTOCOL = "defaultEndpointsProtocol";
    public static final String DEFAULT_PROTOCOL = "http";
    public static final String CONTAINER_NAME = "containerName";
    public static final String FILE_NAME = "fileName";
    public static final String FILE_PATH = "filePath";
    public static final String ENDPOINT_PARAM = "DefaultEndpointsProtocol=http;AccountName=";
    public static final String ACCOUNT_NAME_PARAM = "AccountName=";
    public static final String ACCOUNT_KEY_PARAM = "AccountKey=";
    public static final String PROTOCOL_KEY_PARAM = "DefaultEndpointsProtocol=";
    public static final String SEMICOLON = ";";
    public static final String NAMESPACE = "ns";
    public static final String AZURE_NAMESPACE = "http://org.wso2.esbconnectors.msazurestorageconnector";
    public static final String BLOB = "blob";
    public static final String CONTAINER = "container";
    public static final String RESULT = "result";
    public static final String START_TAG = "<result><success>";
    public static final String END_TAG = "</success></result>";
    public static final String EMPTY_RESULT_TAG = "<result></></result>";

    private AzureConstants() {
    }
}
