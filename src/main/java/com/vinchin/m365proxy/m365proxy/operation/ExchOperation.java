/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	ExchOperation.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.operation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.requests.GraphServiceClient;
import com.vinchin.m365proxy.apis.powershell.PowershellExchangeOperation;
import com.vinchin.m365proxy.apis.soap.SoapBaseRequest;
import com.vinchin.m365proxy.apis.soap.XmlRequestData;
import com.vinchin.m365proxy.common.Util;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import okhttp3.Request;

import com.vinchin.m365proxy.apis.BaseUtil;
import com.vinchin.m365proxy.apis.ews.EwsBaseRequest;
import com.vinchin.m365proxy.apis.ews.FolderRequests;
import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import com.vinchin.m365proxy.apis.graph.exchange.MessageRequests;
import com.vinchin.m365proxy.m365proxy.message.ExchRpcMessageDefine;
import com.vinchin.m365proxy.m365proxy.handler.ExchRpcServerHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExchOperation {
    public String detectEnv(ExchRpcMessageDefine.ExchSerDetectEnvMessage detectEnvMessage) throws Exception {
        String detectResult = "";

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("region", String.valueOf(detectEnvMessage.region));
        organizationAuthParameters.put("username", detectEnvMessage.username);
        organizationAuthParameters.put("password", detectEnvMessage.password);
        organizationAuthParameters.put("domain", detectEnvMessage.domain);
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(detectEnvMessage.username);


//        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();
//        FolderRequests folderRequests = new FolderRequests(ewsClient);
//        String jsonFolder = folderRequests.getAllTypeRootFolder();


        detectResult = "{\"ews\":0,\"graph\":1}";

        return detectResult;
    }

    /**
     * @Description use ews api to connect user in Exchange Server and get ews api connect handle
     * @param connUserMessage
     * @return
     */
    public ExchRpcServerHandler.ExchDataCache exchSerConnUser(ExchRpcMessageDefine.ExchSerConnUserMessage connUserMessage){
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("region", String.valueOf(connUserMessage.region));
        organizationAuthParameters.put("username", connUserMessage.username);
        organizationAuthParameters.put("password", connUserMessage.password);
        organizationAuthParameters.put("domain", connUserMessage.domain);
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(connUserMessage.username);

        //get ews connect handle
        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();
        ExchRpcServerHandler.ExchDataCache dataCache = new ExchRpcServerHandler.ExchDataCache();

        //get soap connect handle
        SoapBaseRequest soapBaseRequest = new SoapBaseRequest(organizationAuthParameters);
        soapBaseRequest.setSoapHttpClient(connUserMessage.username);
        soapBaseRequest.setHttpContext();

        dataCache.ewsClient = ewsClient;
        dataCache.soapClient = soapBaseRequest;
        dataCache.organizationAuthParameters = organizationAuthParameters;
        dataCache.mail = connUserMessage.mail;

        return dataCache;
    }

    public ExchRpcServerHandler.ExchDataCache exchOnConnUser(ExchRpcMessageDefine.ExchOnConnUserMessage connUserMessage) throws Exception {
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("region", String.valueOf(connUserMessage.region));
        organizationAuthParameters.put("tenantUuid", connUserMessage.tenant_Uuid);
        organizationAuthParameters.put("appUuid", connUserMessage.app_uuid);
        organizationAuthParameters.put("appSecret", connUserMessage.app_secret);
        organizationAuthParameters.put("appCertInfo", connUserMessage.app_secret != "" ? null : JSON.toJSONString(connUserMessage.app_cert_info));

        //get ews connect handle
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(connUserMessage.username);
        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();

        //get graph connect handle
        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();
        GraphServiceClient<Request> graphClient = graphBaseRequest.getGraphClient();

        //get soap connect handle
        SoapBaseRequest soapBaseRequest = new SoapBaseRequest(organizationAuthParameters);
        soapBaseRequest.setSoapHttpClient(connUserMessage.username);
        soapBaseRequest.setHttpContext();

        ExchRpcServerHandler.ExchDataCache dataCache = new ExchRpcServerHandler.ExchDataCache();
        dataCache.ewsClient = ewsClient;
        dataCache.graphClient = graphClient;
        dataCache.soapClient = soapBaseRequest;
        dataCache.organizationAuthParameters = organizationAuthParameters;
        dataCache.mail = connUserMessage.mail;

        return dataCache;
    }

    /**
     * @Description get root folder by ews, use for Exchange Server
     * @param ewsClient ews api connect handle
     * @return
     * @throws Exception
     */
    public String getRootFolder(ExchangeService ewsClient, SoapBaseRequest soapClient, String mail) throws Exception {
        FolderRequests folderRequests = new FolderRequests(ewsClient);
        JSONObject rootFolderObject = new JSONObject();
        List<JSONObject> rootFolderListObject = new ArrayList<>();

        //get all mail root folder
        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.Inbox);
        rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.INBOX.getNum());
        rootFolderListObject.add(rootFolderObject);

        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.Drafts);
        rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.DRAFTS.getNum());
        rootFolderListObject.add(rootFolderObject);

        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.SentItems);
        rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.SENTITEMS.getNum());
        rootFolderListObject.add(rootFolderObject);

        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.DeletedItems);
        rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.DELETEDITEMS.getNum());
        rootFolderListObject.add(rootFolderObject);

        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.JunkEmail);
        rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.JUNKEMAIL.getNum());
        rootFolderListObject.add(rootFolderObject);

        //use soap request to get archive/conversationhistory root folder
        com.vinchin.m365proxy.apis.soap.FolderRequests soapFolderRequests = new com.vinchin.m365proxy.apis.soap.FolderRequests(soapClient.getSoapHttpClient(), soapClient.getHttpContext());
        XmlRequestData xmlRequestData = new XmlRequestData();
        String xmlToGetArchiveFolder = xmlRequestData.buildXmlToGetRootFolder(mail, "archive");
        rootFolderObject = soapFolderRequests.getFolder(xmlToGetArchiveFolder);
        if (rootFolderObject != null){
            rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
            rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.ARCHIVE.getNum());
            rootFolderListObject.add(rootFolderObject);
        }

        xmlToGetArchiveFolder = xmlRequestData.buildXmlToGetRootFolder(mail, "conversationhistory");
        rootFolderObject = soapFolderRequests.getFolder(xmlToGetArchiveFolder);
        if (rootFolderObject != null){
            rootFolderObject.put("type", BaseUtil.ExchDataType.MESSAGE.getCode());
            rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.CONVERSATIONHISTORY.getNum());
            rootFolderListObject.add(rootFolderObject);
        }

        //calender
        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.Calendar);
        rootFolderObject.put("type", BaseUtil.ExchDataType.CALENDAR.getCode());
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.CALENDAR.getNum());
        rootFolderListObject.add(rootFolderObject);
        //contact
        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.Contacts);
        rootFolderObject.put("type", BaseUtil.ExchDataType.CONTACT);
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.CONTACTS.getNum());
        rootFolderListObject.add(rootFolderObject);
        //task
        rootFolderObject = folderRequests.getFolderInfo(WellKnownFolderName.Tasks);
        rootFolderObject.put("type", BaseUtil.ExchDataType.TASK);
        rootFolderObject.put("folder_num", BaseUtil.ExchFolderNum.TASKS.getNum());
        rootFolderListObject.add(rootFolderObject);

        return rootFolderListObject.toString();
    }
}
