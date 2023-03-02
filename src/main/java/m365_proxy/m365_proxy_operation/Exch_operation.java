/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Exch_operation.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_proxy_operation;

import apis.BaseUtil;
import apis.ews.EwsBaseRequest;
import apis.ews.FolderRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.exchange.MessageRequests;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.requests.GraphServiceClient;
import m365_proxy.M365_proxy_global_vals;
import m365_proxy.m365_rpc_message.Exch_rpc_message_define;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define;
import m365_proxy.m365_rpc_server_handler.Exch_rpc_server_handler;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Exch_operation {
    public String detectEnv(Exch_rpc_message_define.ExchSerDetectEnvMessage detectEnvMessage) throws Exception {
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
    public Exch_rpc_server_handler.ExchDataCache exchSerConnUser(Exch_rpc_message_define.ExchSerConnUserMessage connUserMessage){
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("region", String.valueOf(connUserMessage.region));
        organizationAuthParameters.put("username", connUserMessage.username);
        organizationAuthParameters.put("password", connUserMessage.password);
        organizationAuthParameters.put("domain", connUserMessage.domain);
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(connUserMessage.username);

        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();
        Exch_rpc_server_handler.ExchDataCache dataCache = new Exch_rpc_server_handler.ExchDataCache();
        dataCache._ewsClient = ewsClient;
        dataCache._organizationAuthParameters = organizationAuthParameters;
        dataCache._mail = connUserMessage.mail;

        return dataCache;
    }

    public Exch_rpc_server_handler.ExchDataCache exchOnConnUser(Exch_rpc_message_define.ExchOnConnUserMessage connUserMessage) throws Exception {
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

        Exch_rpc_server_handler.ExchDataCache dataCache = new Exch_rpc_server_handler.ExchDataCache();
        dataCache._ewsClient = ewsClient;
        dataCache._graphClient = graphClient;
        dataCache._organizationAuthParameters = organizationAuthParameters;
        dataCache._mail = connUserMessage.mail;

        return dataCache;
    }

    /**
     * @Description get root folder by ews, use for Exchange Server
     * @param ewsClient ews api connect handle
     * @return
     * @throws Exception
     */
    public String getRootFolder(ExchangeService ewsClient) throws Exception {
        FolderRequests folderRequests = new FolderRequests(ewsClient);
        List<String> rootFolderListObject = new ArrayList<>();

        //get all mail root folder
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Inbox, BaseUtil.ExchDataType.MESSAGE.getCode(), BaseUtil.ExchFolderNum.INBOX.getNum()));
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Drafts, 0, BaseUtil.ExchFolderNum.DRAFTS.getNum()));
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.SentItems, 0, BaseUtil.ExchFolderNum.SENTITEMS.getNum()));
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.DeletedItems, 0, BaseUtil.ExchFolderNum.DELETEDITEMS.getNum()));
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.JunkEmail, 0, BaseUtil.ExchFolderNum.JUNKEMAIL.getNum()));
        //calender
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Calendar, 1, BaseUtil.ExchFolderNum.CALENDAR.getNum()));
        //contact
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Contacts, 2, BaseUtil.ExchFolderNum.CONTACTS.getNum()));
        //task
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Tasks, 3, BaseUtil.ExchFolderNum.TASKS.getNum()));

        return rootFolderListObject.toString();
    }

    /**
     * @Description get root folder by graph, use for Exchange Online
     * @param graphClient graph api connect handle
     * @return
     */
    public String getRootFolder(ExchangeService ewsClient, GraphServiceClient<Request> graphClient, String mail) throws Exception {
        List<String> rootFolderListObject = new ArrayList<>();

        MessageRequests messageRequests = new MessageRequests(graphClient, mail);
        FolderRequests folderRequests = new FolderRequests(ewsClient);

        //get all mail root folder
        rootFolderListObject.add(messageRequests.getFolderInfo("inbox", BaseUtil.ExchDataType.MESSAGE.getCode(), BaseUtil.ExchFolderNum.INBOX.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("drafts", 0, BaseUtil.ExchFolderNum.DRAFTS.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("sentitems", 0, BaseUtil.ExchFolderNum.SENTITEMS.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("deleteditems", 0, BaseUtil.ExchFolderNum.DELETEDITEMS.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("junkemail", 0, BaseUtil.ExchFolderNum.JUNKEMAIL.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("archive", 0, BaseUtil.ExchFolderNum.ARCHIVE.getNum()));
        rootFolderListObject.add(messageRequests.getFolderInfo("conversationhistory", 0, BaseUtil.ExchFolderNum.CONVERSATIONHISTORY.getNum()));

        //calender
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Calendar, 1, BaseUtil.ExchFolderNum.CALENDAR.getNum()));
        //contact
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Contacts, 2, BaseUtil.ExchFolderNum.CONTACTS.getNum()));
        //task
        rootFolderListObject.add(folderRequests.getFolderInfo(WellKnownFolderName.Tasks, 3, BaseUtil.ExchFolderNum.TASKS.getNum()));

        return rootFolderListObject.toString();
    }
}
