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

import com.alibaba.fastjson.JSONObject;
import com.vinchin.m365proxy.apis.ews.EwsBaseRequest;
import com.vinchin.m365proxy.apis.ews.FolderRequests;
import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import com.vinchin.m365proxy.apis.graph.common.UserRequests;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

import static com.vinchin.m365proxy.m365proxy.M365ProxyError.M365ErrorCode.M365_DETECT_ENV_GENERIC_ERROR;

public class M365CommonOperation {
    public String detectM365Env(M365CommonRpcMessageDefine.M365CommonDetectEnvMessage m365CommonDetectEnvMessage) throws Exception {
        JSONObject allDetectResult = new JSONObject();
        JSONObject oneStepResult = new JSONObject();
        int step = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", m365CommonDetectEnvMessage.tenant_Uuid);
        organizationAuthParameters.put("appUuid", m365CommonDetectEnvMessage.app_uuid);
        organizationAuthParameters.put("appSecret", m365CommonDetectEnvMessage.app_secret);
        organizationAuthParameters.put("region", String.valueOf(m365CommonDetectEnvMessage.region));
        organizationAuthParameters.put("appCertInfo", m365CommonDetectEnvMessage.app_secret != "" ? null : JSON.toJSONString(m365CommonDetectEnvMessage.app_cert_info));

        //init graph
        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();

        //init ews
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(m365CommonDetectEnvMessage.username);

        //first step:check graph api
        step++;
        UserRequests userRequests = new UserRequests(graphBaseRequest.getGraphClient());
        String userListJson = userRequests.getUserList();
        if (userListJson == ""){
            oneStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
            allDetectResult.put(Integer.toString(step), oneStepResult);
            return allDetectResult.toString();
        }

        //second step: check ews api
        step++;
        FolderRequests folderRequests = new FolderRequests(ewsBaseRequest.getEwsClient());
        folderRequests.getFolderInfo(WellKnownFolderName.Inbox);

        //third step: check ews api
        step++;




        //four step: check username's roles(ApplicationImpersonation)
        step++;
        JSONObject userInfoJsonObject = JSON.parseObject(userListJson);
        for (Map.Entry<String, Object> entry : userInfoJsonObject.entrySet()) {
            JSONObject oneUserInfo = (JSONObject) entry.getValue();
            String mail = (String) oneUserInfo.get("mail");
            if (mail != m365CommonDetectEnvMessage.username) {
                ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
                ewsBaseRequest.setEwsClient(mail);
                folderRequests = new FolderRequests(ewsBaseRequest.getEwsClient());
                folderRequests.getFolderInfo(WellKnownFolderName.Inbox);
            }
        }

        return allDetectResult.toString();
    }
}
