/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	FolderRequests.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/13
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.apis.soap;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FolderRequests extends SoapBaseRequest {

    public FolderRequests(List<Map> soapClientList) {
        soapClient = (HttpPost) soapClientList.get(0).get("soapClient");
        httpContext = (HttpClientContext) soapClientList.get(1).get("httpContext");
    }

    public JSONObject getFolder(String xml){
        JSONObject folderInfo = new JSONObject();
        String responseXmlData = doSoapRequest(xml);
        if (responseXmlData == null){
            return null;
        }

        try{
            Document xmlDocument = DocumentHelper.parseText(responseXmlData);
            if (xmlDocument != null){
                Element rootElement = xmlDocument.getRootElement();
                Element responseMessageElement = rootElement.element("Body").element("GetFolderResponse").element("ResponseMessages").element("GetFolderResponseMessage");
                Element responseCodeElement = responseMessageElement.element("ResponseCode");
                if (!Objects.equals(responseCodeElement.getText(), "NoError")){
                    return null;
                } else {
                    Element folderInfoElement = responseMessageElement.element("Folders").element("Folder");
                    List<Element> folderInfoList = folderInfoElement.elements();
                    for(Element subElement : folderInfoList){
                        String name = subElement.getName();
                        if (Objects.equals(name, "FolderId")){
                            folderInfo.put("folder_id", subElement.attribute("Id").getText());
                        }
                        if (Objects.equals(name, "ParentFolderId")){
                            folderInfo.put("parent_folder_id", subElement.attribute("Id").getText());
                        }
                        if (Objects.equals(name, "DisplayName")){
                            folderInfo.put("display_name", subElement.getText());
                        }
                        if (Objects.equals(name, "ChildFolderCount")){
                            folderInfo.put("child_folder_name", subElement.getText());
                        }
                    }
                }
            }
        } catch (Exception e){
            folderInfo.clear();
            return null;
        } finally {
            forceDestroyHttpResource();
        }

        return folderInfo;
    }


}
