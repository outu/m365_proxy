/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	ExchRpcPacketHandler.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


public class ExchRpcPacketHandler {
    public static class toMessage{
        public static ExchRpcMessageDefine.ExchSerDetectEnvMessage TransformDetectEnvInfo(String detectEnvInfo){
            ExchRpcMessageDefine.ExchSerDetectEnvMessage detectEnvMessage;
            JSONObject detectEnvInfoJsonObject = new JSONObject(JSON.parseObject(detectEnvInfo));
            detectEnvMessage = JSON.toJavaObject(detectEnvInfoJsonObject, ExchRpcMessageDefine.ExchSerDetectEnvMessage.class);

            return detectEnvMessage;
        }

        public static ExchRpcMessageDefine.ExchSerConnUserMessage TransformServerConnUserInfo(String connUserInfo){
            ExchRpcMessageDefine.ExchSerConnUserMessage connUserMessage;
            JSONObject connUserInfoJsonObject = new JSONObject(JSON.parseObject(connUserInfo));
            connUserMessage = JSON.toJavaObject(connUserInfoJsonObject, ExchRpcMessageDefine.ExchSerConnUserMessage.class);

            return connUserMessage;
        }

        public static ExchRpcMessageDefine.ExchOnConnUserMessage TransformOnlineConnUserInfo(String connUserInfo){
            ExchRpcMessageDefine.ExchOnConnUserMessage connUserMessage;
            JSONObject connUserInfoJsonObject = new JSONObject(JSON.parseObject(connUserInfo));
            connUserMessage = JSON.toJavaObject(connUserInfoJsonObject, ExchRpcMessageDefine.ExchOnConnUserMessage.class);

            return connUserMessage;
        }
    }
}
