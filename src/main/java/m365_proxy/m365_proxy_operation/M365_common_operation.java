/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_common_operation.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_proxy_operation;

import apis.ews.EwsBaseRequest;
import apis.ews.FolderRequests;
import com.alibaba.fastjson.JSON;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define;
import microsoft.exchange.webservices.data.core.ExchangeService;

import java.util.HashMap;
import java.util.Map;

public class M365_common_operation {
    public String detectM365Env(M365_common_rpc_message_define.M365CommonDetectEnvMessage m365CommonDetectEnvMessage) throws Exception {
        String detectResult = "";

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", m365CommonDetectEnvMessage.tenant_Uuid);
        organizationAuthParameters.put("appUuid", m365CommonDetectEnvMessage.app_uuid);
        organizationAuthParameters.put("appSecret", m365CommonDetectEnvMessage.app_secret);
        organizationAuthParameters.put("region", String.valueOf(m365CommonDetectEnvMessage.region));
        organizationAuthParameters.put("appCertInfo", m365CommonDetectEnvMessage.app_secret != "" ? null : JSON.toJSONString(m365CommonDetectEnvMessage.app_cert_info));
        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(m365CommonDetectEnvMessage.username);


        ExchangeService ewsClient = ewsBaseRequest.getEwsClient();
        FolderRequests folderRequests = new FolderRequests(ewsClient);
        String jsonFolder = folderRequests.getAllTypeRootFolder();



        detectResult = "{\"ews\":0,\"graph\":1}";

        return jsonFolder;
    }
}
