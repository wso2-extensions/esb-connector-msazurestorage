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
package org.wso2.carbon.connector.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPBody;
import org.apache.commons.lang.StringUtils;
import org.apache.synapse.MessageContext;

import javax.xml.stream.XMLStreamException;
import java.util.Iterator;

/**
 * This class for creating the payload.
 */
public class ResultPayloadCreate {

    /**
     * Prepare payload.
     *
     * @param messageContext The message context that is processed by a handler in the handle method.
     * @param element        OMElement.
     */
    public void preparePayload(MessageContext messageContext, OMElement element) {
        SOAPBody soapBody = messageContext.getEnvelope().getBody();
        for (Iterator itr = soapBody.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            child.detach();
        }
        for (Iterator itr = element.getChildElements(); itr.hasNext(); ) {
            OMElement child = (OMElement) itr.next();
            soapBody.addChild(child);
        }
    }

    /**
     * Create a OMElement.
     *
     * @param output output.
     * @return return resultElement.
     * @throws XMLStreamException if XML exception occurs.
     */
    public OMElement performSearchMessages(String output) throws XMLStreamException {
        OMElement resultElement;
        if (StringUtils.isNotEmpty(output)) {
            resultElement = AXIOMUtil.stringToOM(output);
        } else {
            resultElement = AXIOMUtil.stringToOM(AzureConstants.EMPTY_RESULT_TAG);
        }
        return resultElement;
    }
}