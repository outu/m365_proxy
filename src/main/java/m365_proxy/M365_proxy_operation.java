/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_operation.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365CommonDetectEnvMessage;

public class M365_proxy_operation {
    public String detectM365Env(M365CommonDetectEnvMessage m365CommonDetectEnvMessage){
        String detectResult = "";

        detectResult = "{\"ews\":0,\"graph\":1}";

        return detectResult;
    }

    public String getUserInfo(){
        return "";
    }
}
