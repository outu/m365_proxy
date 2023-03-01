/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_common_rpc_server_handler.java: M365 common rpc server handler class
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_server_handler;

import common.TypeConversion;
import m365_proxy.M365_proxy_rpc_server;
import m365_proxy.m365_proxy_operation.M365_common_operation;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define;
import m365_proxy.m365_rpc_message.M365_common_rpc_packet_handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class M365_common_rpc_server_handler extends M365_proxy_rpc_server {
    public static final Logger logger = LoggerFactory.getLogger(M365_common_rpc_server_handler.class);
    /**
     * @Description handle rpc m365 common packet
     * @param m365CommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcM365CommonPacket(int m365CommonRpcCode, ByteBuffer byteBuffer, long length){
        boolean ret = true;

        switch (M365_common_rpc_message_define.M365CommonRpcOpcode.getOpCodeEnum(m365CommonRpcCode)){
            case M365_COMMON_RPC_OPCODE_DETECT_ENV:
                ret = handleDetectM365Env(byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_USER_LIST:
               // ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_GROUP_LIST:
               // ret = handleRpcM365CommonPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_CONNECT_USER:
                //ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_CONNECT_GROUP:
               // ret = handleRpcM365CommonPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_IS_USER_EXISTS:
               // ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            default:
                ret = false;
                logger.warn("unknown M365 common rpc opcode.");
                break;
        }

        return ret;
    }

    /**
     * @deprecated Environmental detection of Microsoft 365
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleDetectM365Env(ByteBuffer byteBuffer, long length){
        boolean ret = true;

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            M365_common_rpc_message_define.M365CommonDetectEnvMessage message = M365_common_rpc_packet_handler.toMessage.TransformM365CommonDetectEnvInfo(jsonString);

            M365_common_operation m365CommonOp = new M365_common_operation();
            String detectM365EnvAskInfo = m365CommonOp.detectM365Env(message);
            byte[] askInfo = TypeConversion.stringToBytes(detectM365EnvAskInfo);
            buildAndSendAskMsg(M365_common_rpc_message_define.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), askInfo);

        } catch (Exception e){
            logger.error("handle detect m365 env failed: " + e.getMessage());
            //send error
            ret = false;
        }

        return ret;
    }

    public void destroy(){
        _clientAttachment = null;
        _clientChannel = null;
    }

}