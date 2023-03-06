/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365CommonOperation.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.operation;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import com.vinchin.m365proxy.apis.graph.common.UserRequests;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine;

public class M365CommonOperation {
    public String detectM365Env(M365CommonRpcMessageDefine.M365CommonDetectEnvMessage m365CommonDetectEnvMessage) throws Exception {
        String detectResult = "";

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", m365CommonDetectEnvMessage.tenant_Uuid);
        organizationAuthParameters.put("appUuid", m365CommonDetectEnvMessage.app_uuid);
        organizationAuthParameters.put("appSecret", m365CommonDetectEnvMessage.app_secret);
        organizationAuthParameters.put("region", String.valueOf(m365CommonDetectEnvMessage.region));
        organizationAuthParameters.put("appCertInfo", m365CommonDetectEnvMessage.app_secret != "" ? null : JSON.toJSONString(m365CommonDetectEnvMessage.app_cert_info));
        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();
        UserRequests userRequests = new UserRequests(graphBaseRequest.getGraphClient());
        String userInfo = userRequests.syncUserInfo("", "");



//        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
//        ewsBaseRequest.setEwsClient(m365CommonDetectEnvMessage.username);
//
//
//        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();
//        FolderRequests folderRequests = new FolderRequests(ewsClient);
//        String jsonFolder = folderRequests.getAllTypeRootFolder();



        detectResult = "{\"ews\":0,\"graph\":1}";

        return userInfo;
    }
}
