/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyRpcPacketHandler.java: It provides operations of parse packet
 *                  which from m365_client_manager and m365_client_transfer.
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.message;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine.M365CommonDetectEnvMessage;

public class M365CommonRpcPacketHandler {
    public static class toMessage{
        public static M365CommonDetectEnvMessage TransformM365CommonDetectEnvInfo(String detectEnvInfo){
            M365CommonDetectEnvMessage m365CommonDetectEnvMessage;
            JSONObject detectEnvInfoJsonObject = new JSONObject(JSON.parseObject(detectEnvInfo));
            m365CommonDetectEnvMessage = JSON.toJavaObject(detectEnvInfoJsonObject, M365CommonDetectEnvMessage.class);

            return m365CommonDetectEnvMessage;
        }
    }

    public static class toJson{

    }
}
