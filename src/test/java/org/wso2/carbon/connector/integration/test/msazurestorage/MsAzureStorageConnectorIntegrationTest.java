/*
*  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector.integration.test.msazurestorage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Microsoft azure storage connector integration test
 */
public class MsAzureStorageConnectorIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> eiRequestHeadersMap = new HashMap<String, String>();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {

        String connectorName = System.getProperty("connector_name") + "-connector-" +
                System.getProperty("connector_version") + ".zip";
        init(connectorName);
        eiRequestHeadersMap.put("Accept-Charset", "UTF-8");
        eiRequestHeadersMap.put("Content-Type", "application/json");
    }

    @Test(enabled = true, groups = {"wso2.ei"},
            description = "msazurestorage {createContainer} integration test with mandatory parameter.")
    public void testCreateContainerWithMandatoryParameters() throws Exception {

        eiRequestHeadersMap.put("Action", "urn:createContainer");
        RestResponse<JSONObject> eiRestResponse = sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap,
                "createContainer.json");
        Assert.assertEquals(true, eiRestResponse.getBody().toString().contains("true"));
    }

    @Test(enabled = true, groups = {"wso2.ei"}, dependsOnMethods = {"testCreateContainerWithMandatoryParameters"},
            description = "msazurestorage {listContainers} integration test with mandatory parameter.")
    public void testListContainersWithMandatoryParameters() throws Exception {

        eiRequestHeadersMap.put("Action", "urn:listContainers");
        RestResponse<JSONObject> eiRestResponse = sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap,
                "listContainers.json");
        JSONArray jsonArray = eiRestResponse.getBody().getJSONObject("result").getJSONArray("container");
        boolean state = false;
        for (int i = 0 ; i < jsonArray.length(); i++) {
            String name = jsonArray.getString(i);
            if (name.equals(connectorProperties.getProperty("containerName"))){
                state = true;
                break;
            }
        }
        Assert.assertTrue(state);
    }

    @Test(enabled = true, groups = {"wso2.ei"}, dependsOnMethods = {"testListContainersWithMandatoryParameters"},
            description = "msazurestorage {uploadBlob} integration test with mandatory parameter.")
    public void testUploadBlobWithMandatoryParameters() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("artifacts/ESB/config/resources/msazurestorage/" +
                "sampleResource.txt").getFile());
        String absolutePath = file.getAbsolutePath();

        connectorProperties.put("filePath", absolutePath);
        eiRequestHeadersMap.put("Action", "urn:uploadBlob");
        RestResponse<JSONObject> eiRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap, "uploadBlob.json");
        Assert.assertEquals(true, eiRestResponse.getBody().toString().contains("true"));
    }

    @Test(enabled = true, groups = {"wso2.ei"}, dependsOnMethods = {"testUploadBlobWithMandatoryParameters"},
            description = "msazurestorage {listBlobs} integration test with mandatory parameter.")
    public void testListBlobsWithMandatoryParameters() throws Exception {

        eiRequestHeadersMap.put("Action", "urn:listBlobs");
        RestResponse<JSONObject> eiRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap, "listBlobs.json");
        String blopsFromEI = eiRestResponse.getBody().getJSONObject("result").toString();
        Assert.assertTrue(blopsFromEI.contains(connectorProperties.getProperty("containerName")));
    }
    
    @Test(enabled = true, groups = {"wso2.ei"}, dependsOnMethods = {"testListBlobsWithMandatoryParameters"},
            description = "msazurestorage {deleteBlob} integration test with mandatory parameter.")
    public void testDeleteBlobWithMandatoryParameters() throws Exception {

        eiRequestHeadersMap.put("Action", "urn:deleteBlob");
        RestResponse<JSONObject> eiRestResponse =
                sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap, "deleteBlob.json");
        Assert.assertEquals(true, eiRestResponse.getBody().toString().contains("true"));
    }

    @Test(enabled = true, groups = {"wso2.ei"}, dependsOnMethods = {"testDeleteBlobWithMandatoryParameters"},
            description = "msazurestorage {deleteContainer} integration test with mandatory parameter.")
    public void testDeleteContainerWithMandatoryParameters() throws Exception {

        eiRequestHeadersMap.put("Action", "urn:deleteContainer");
        RestResponse<JSONObject> eiRestResponse = sendJsonRestRequest(proxyUrl, "POST", eiRequestHeadersMap,
                "deleteContainer.json");
        Assert.assertEquals(true, eiRestResponse.getBody().toString().contains("true"));
    }
}