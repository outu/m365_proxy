/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365CommonRpcServerHandler.java: M365 common rpc server handler class
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.handler;

import com.vinchin.m365proxy.common.TypeConversion;
import com.vinchin.m365proxy.m365proxy.M365ProxyRpcServer;
import com.vinchin.m365proxy.m365proxy.operation.M365CommonOperation;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcPacketHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_ERROR;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_SUCCESS;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.M365ErrorCode.M365_RPC_MSG_ERROR;
import static com.vinchin.m365proxy.m365proxy.message.BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PUBLIC;


public class M365CommonRpcServerHandler extends M365ProxyRpcServer {
    public static final Logger logger = LoggerFactory.getLogger(M365CommonRpcServerHandler.class);
    /**
     * @Description handle rpc m365 common packet
     * @param m365CommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public int handleRpcM365CommonPacket(int m365CommonRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();

        M365CommonRpcMessageDefine.M365CommonRpcOpcode m365CommonRpcOpcode= M365CommonRpcMessageDefine.M365CommonRpcOpcode.getOpCodeEnum(m365CommonRpcCode);
        switch (m365CommonRpcOpcode){
            case M365_COMMON_RPC_OPCODE_DETECT_ENV:
                ret = handleDetectM365Env(byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_USER_LIST:
               // ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_GROUP_LIST:
               // ret = handleRpcM365CommonPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_IS_USER_EXISTS:
               // ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            default:
                ret = M365_RPC_MSG_ERROR.getCode();
                ret = sendAskHeader(BD_RPC_OP_TYPE_PUBLIC.getCode(), m365CommonRpcCode, ret);
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
    private int handleDetectM365Env(ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            M365CommonRpcMessageDefine.M365CommonDetectEnvMessage message = M365CommonRpcPacketHandler.toMessage.TransformM365CommonDetectEnvInfo(jsonString);

            M365CommonOperation m365CommonOp = new M365CommonOperation();
            String detectM365EnvAskInfo = m365CommonOp.detectM365Env(message);
            byte[] askInfo = TypeConversion.stringToBytes(detectM365EnvAskInfo);
            buildAndSendAskMsg(M365CommonRpcMessageDefine.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), askInfo, 0);
        } catch (Exception e){
            logger.error("handle detect m365 env failed: " + e.getMessage());
            //send error
            ret = BD_GENERIC_ERROR.getCode();
        }

        return ret;
    }

    public void destroy(){
        _clientAttachment = null;
        _clientChannel = null;
    }

}
