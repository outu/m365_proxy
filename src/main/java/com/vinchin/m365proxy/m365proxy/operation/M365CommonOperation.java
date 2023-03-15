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

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.vinchin.m365proxy.apis.ews.EwsBaseRequest;
import com.vinchin.m365proxy.apis.ews.FolderRequests;
import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import com.vinchin.m365proxy.apis.graph.common.ApplicationRequests;
import com.vinchin.m365proxy.apis.graph.common.UserRequests;
import com.vinchin.m365proxy.common.Util;
import com.vinchin.m365proxy.m365proxy.M365ProxyError;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;

import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_SUCCESS;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.M365ErrorCode.M365_DETECT_ENV_GENERIC_ERROR;

public class M365CommonOperation {
    public final static List<String> exchNeedAccessResources = Arrays.asList(
            "3b5f3d61-589b-4a3c-a359-5dd4b5ee5bd5",
            "dc890d15-9560-4a4c-9b7f-a736ec74ec40",
            "7427e0e9-2fba-42fe-b0c0-848c9e6a8182",
            "06da0dbc-49e2-44d2-8312-53f166ab848a",
            "c5366453-9fb0-48a5-a156-24f0c49a4b84",
            //"5b567255-7703-4780-807c-7be8301ae99b",
            "7ab1d382-f21e-4acd-a863-ba3e13f7da61"
    );

    /**
     * @Description detect m365 env
     * 1、test graph connect
     * 2、test ews connect(Exchange)
     * 3、test azure ad app
     * 4、test admin user role
     * @param m365CommonDetectEnvMessage
     * @return
     * @throws Exception
     */
    public String detectM365Env(M365CommonRpcMessageDefine.M365CommonDetectEnvMessage m365CommonDetectEnvMessage) throws Exception {
        JSONObject allDetectResult = new JSONObject();
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
        JSONObject firstStepResult = new JSONObject();
        if (Util.jsonStringIsEmpty(userListJson)){
            firstStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
            allDetectResult.put(Integer.toString(step), firstStepResult);
            return allDetectResult.toString();
        } else {
            firstStepResult.put("error_code", BD_GENERIC_SUCCESS.getCode());
            allDetectResult.put(Integer.toString(step), firstStepResult);
        }

        //second step: check ews api
        step++;
        FolderRequests folderRequests = new FolderRequests(ewsBaseRequest.getEwsClient());
        JSONObject folderInfoJsonObject = folderRequests.getFolderInfo(WellKnownFolderName.Inbox);
        JSONObject secondStepResult = new JSONObject();
        if (Util.jsonStringIsEmpty(folderInfoJsonObject.toString())){
            secondStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
            allDetectResult.put(Integer.toString(step), secondStepResult);
            return allDetectResult.toString();
        } else {
            secondStepResult.put("error_code", BD_GENERIC_SUCCESS.getCode());
            allDetectResult.put(Integer.toString(step), secondStepResult);
        }

        //third step: check Azure AD app access resources
        step++;
        ApplicationRequests applicationRequests = new ApplicationRequests(graphBaseRequest.getGraphClient());
        JSONObject thirdStepResult = new JSONObject();
        List<String> appAccessResources = applicationRequests.getAzureADAppAuthResourceAccess(m365CommonDetectEnvMessage.app_uuid);
        if (appAccessResources.size() == 0){
            thirdStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
            allDetectResult.put(Integer.toString(step), thirdStepResult);
            return allDetectResult.toString();
        } else {
            for(int i=0; i<exchNeedAccessResources.size(); i++){
                if (!appAccessResources.contains(exchNeedAccessResources.get(i))){
                    thirdStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
                    allDetectResult.put(Integer.toString(step), thirdStepResult);
                    return allDetectResult.toString();
                }
            }
        }
        thirdStepResult.put("error_code", BD_GENERIC_SUCCESS.getCode());
        allDetectResult.put(Integer.toString(step), thirdStepResult);

        //four step: check username's roles(ApplicationImpersonation)
        step++;

        List<JSONObject> userList = JSON.parseObject(userListJson, new TypeReference<ArrayList<JSONObject>>(){});
        JSONObject fourStepResult = new JSONObject();
        for(int i=0; i<userList.size(); i++){
            JSONObject oneUserInfo = userList.get(i);
            String mail = (String) oneUserInfo.get("mail");
            if (!Objects.equals(mail, m365CommonDetectEnvMessage.username)) {
                ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
                ewsBaseRequest.setEwsClient(mail);
                folderRequests = new FolderRequests(ewsBaseRequest.getEwsClient());
                folderInfoJsonObject = folderRequests.getFolderInfo(WellKnownFolderName.Inbox);
                if (Util.jsonStringIsEmpty(folderInfoJsonObject.toString())){
                    fourStepResult.put("error_code", M365_DETECT_ENV_GENERIC_ERROR.getCode());
                    allDetectResult.put(Integer.toString(step), fourStepResult);
                    return allDetectResult.toString();
                }
                break;
            }
        }

        fourStepResult.put("error_code", BD_GENERIC_SUCCESS.getCode());
        allDetectResult.put(Integer.toString(step), fourStepResult);

        return allDetectResult.toString();
    }
}
