/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Exch_rpc_packet_handler.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import m365_proxy.m365_rpc_message.Exch_rpc_message_define;


public class Exch_rpc_packet_handler {
    public static class toMessage{
        public static Exch_rpc_message_define.ExchSerDetectEnvMessage TransformDetectEnvInfo(String detectEnvInfo){
            Exch_rpc_message_define.ExchSerDetectEnvMessage detectEnvMessage;
            JSONObject detectEnvInfoJsonObject = new JSONObject(JSON.parseObject(detectEnvInfo));
            detectEnvMessage = JSON.toJavaObject(detectEnvInfoJsonObject, Exch_rpc_message_define.ExchSerDetectEnvMessage.class);

            return detectEnvMessage;
        }

        public static Exch_rpc_message_define.ExchSerConnUserMessage TransformServerConnUserInfo(String connUserInfo){
            Exch_rpc_message_define.ExchSerConnUserMessage connUserMessage;
            JSONObject connUserInfoJsonObject = new JSONObject(JSON.parseObject(connUserInfo));
            connUserMessage = JSON.toJavaObject(connUserInfoJsonObject, Exch_rpc_message_define.ExchSerConnUserMessage.class);

            return connUserMessage;
        }

        public static Exch_rpc_message_define.ExchOnConnUserMessage TransformOnlineConnUserInfo(String connUserInfo){
            Exch_rpc_message_define.ExchOnConnUserMessage connUserMessage;
            JSONObject connUserInfoJsonObject = new JSONObject(JSON.parseObject(connUserInfo));
            connUserMessage = JSON.toJavaObject(connUserInfoJsonObject, Exch_rpc_message_define.ExchOnConnUserMessage.class);

            return connUserMessage;
        }
    }
}
