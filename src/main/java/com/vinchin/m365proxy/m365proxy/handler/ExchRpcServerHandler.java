/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	ExchRpcServerHandler.java: Exchange Online&Exchange Server rpc server handler class
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.handler;

import com.microsoft.graph.requests.GraphServiceClient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.vinchin.m365proxy.apis.BaseUtil;
import com.vinchin.m365proxy.common.TypeConversion;
import com.vinchin.m365proxy.m365proxy.M365ProxyGlobalVals;
import com.vinchin.m365proxy.m365proxy.M365ProxyRpcServer;
import com.vinchin.m365proxy.m365proxy.operation.ExchOperation;
import com.vinchin.m365proxy.m365proxy.message.BdRpcMessageDefine;
import com.vinchin.m365proxy.m365proxy.message.ExchRpcMessageDefine;
import com.vinchin.m365proxy.m365proxy.message.ExchRpcPacketHandler;
import com.vinchin.m365proxy.m365proxy.message.M365CommonRpcMessageDefine;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_ERROR;
import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_SUCCESS;
import static com.vinchin.m365proxy.m365proxy.message.BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PUBLIC;


public class ExchRpcServerHandler extends M365ProxyRpcServer {
    public static final Logger logger = LoggerFactory.getLogger(ExchRpcServerHandler.class);
    /**
     * @Description handle Exchange online&Exchange Server packet
     * @param exchRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public int handleRpcExchPacket(int exchRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        ExchRpcMessageDefine.ExchRpcOpType exchRpcOpType = getExchRpcOpType(exchRpcCode);

        switch (exchRpcOpType){
            case EXCH_RPC_OP_TYPE_MAIL:
                ret = handleRpcExchMailPacket(exchRpcCode, byteBuffer, length);
                break;
            case EXCH_RPC_OP_TYPE_APPOINTMENT:
                ret = handleRpcExchAppointmentPacket(exchRpcCode, byteBuffer, length);
                break;
            case EXCH_RPC_OP_TYPE_CONTACT:
                ret = handleRpcExchContactPacket(exchRpcCode, byteBuffer, length);
                break;
            case EXCH_RPC_OP_TYPE_TASK:
                ret = handleRpcExchTaskPacket(exchRpcCode, byteBuffer, length);
                break;
            default:
                ret = handleRpcExchCommonPacket(exchRpcCode, byteBuffer, length);
                break;
        }

        return ret;
    }

    /**
     * @Description Exchange Online&Exhchange Server type classification of packet according to opcode, like mail|appointment|contact|task
     * @param exchOpcode
     * @return
     */
    private ExchRpcMessageDefine.ExchRpcOpType getExchRpcOpType(int exchOpcode){
        if (250 <= exchOpcode && exchOpcode < 300){
            return ExchRpcMessageDefine.ExchRpcOpType.EXCH_RPC_OP_TYPE_MAIL;
        } else if (300 <= exchOpcode && exchOpcode < 350) {
            return ExchRpcMessageDefine.ExchRpcOpType.EXCH_RPC_OP_TYPE_APPOINTMENT;
        } else if (350 <= exchOpcode && exchOpcode < 400) {
            return ExchRpcMessageDefine.ExchRpcOpType.EXCH_RPC_OP_TYPE_CONTACT;
        } else if (400 <= exchOpcode && exchOpcode < 450) {
            return ExchRpcMessageDefine.ExchRpcOpType.EXCH_RPC_OP_TYPE_TASK;
        } else {
            return ExchRpcMessageDefine.ExchRpcOpType.EXCH_RPC_OP_TYPE_COMMON;
        }
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param mailRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private int handleRpcExchMailPacket(int mailRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        logger.debug("handle private->m365 common-> mail packet....");
        return ret;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param appointmentRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private int handleRpcExchAppointmentPacket(int appointmentRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        logger.debug("handle private->m365 exch-> appointment packet....");
        return ret;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param contactRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private int handleRpcExchContactPacket(int contactRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        logger.debug("handle private->m365 exch-> contact packet....");
        return ret;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param taskRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private int handleRpcExchTaskPacket(int taskRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        logger.debug("handle private->m365 exch-> task packet....");
        return ret;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param exchCommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private int handleRpcExchCommonPacket(int exchCommonRpcCode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        ExchRpcMessageDefine.ExchRpcOpcode exchRpcOpcode = ExchRpcMessageDefine.ExchRpcOpcode.getOpCodeEnum(exchCommonRpcCode);

        switch (exchRpcOpcode){
            case EXCH_RPC_OPCODE_DETECT_ENV:
                ret = handleDetectEnv(byteBuffer, length);
                break;
            case EXCH_RPC_OPCODE_SERVICE_CONNECT_USER:
                ret = handleServiceConnectUser(byteBuffer, length);
                break;
            case EXCH_RPC_OPCODE_ONLINE_CONNECT_USER:
                ret = handleOnlineConnectUser(byteBuffer, length);
                break;
            case EXCH_RPC_OPCODE_GET_ROOT_FOLDER:
                ret = handleGetRootFolder();
                break;
            default:
                //Because the number of opcode has been limited previously, it is impossible to have an unknown number,
                //so default can never be executed here
                logger.warn("unknown M365 exch rpc opcode.");
                ret = BD_GENERIC_ERROR.getCode();
                ret = sendAskHeader(BD_RPC_OP_TYPE_PUBLIC.getCode(), exchCommonRpcCode, ret);
                break;
        }

        return ret;
    }

    private int handleDetectEnv(ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            ExchRpcMessageDefine.ExchSerDetectEnvMessage message = ExchRpcPacketHandler.toMessage.TransformDetectEnvInfo(jsonString);

            ExchOperation exchOp = new ExchOperation();
            String detectEnvAskInfo = exchOp.detectEnv(message);
            byte[] askInfo = TypeConversion.stringToBytes(detectEnvAskInfo);
            ret = buildAndSendAskMsg(M365CommonRpcMessageDefine.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), askInfo, 0);
        } catch (Exception e){
            logger.error("handle detect m365 env failed: " + e.getMessage());
            ret = sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_DETECT_ENV.getOpCode(), ret);
            //force set error
            if (BD_GENERIC_SUCCESS.getCode() == ret){
                ret = BD_GENERIC_ERROR.getCode();
            }
        }

        return ret;
    }

    private int handleServiceConnectUser(ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        String mail = "";

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            ExchRpcMessageDefine.ExchSerConnUserMessage message = ExchRpcPacketHandler.toMessage.TransformServerConnUserInfo(jsonString);
            mail = message.mail;
            ExchOperation exchOp = new ExchOperation();
            _exchDataCache = exchOp.exchSerConnUser(message);
            addGlobalCache(message.thread_uuid);

            ret = sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_SERVICE_CONNECT_USER.getOpCode(), 0);
        } catch (Exception e){
            logger.error("handle Exchange Server connect user: " + mail +  " failed: " + e.getMessage());
            sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_SERVICE_CONNECT_USER.getOpCode(), ret);
            //force set error
            if (BD_GENERIC_SUCCESS.getCode() == ret){
                ret = BD_GENERIC_ERROR.getCode();
            }
        }

        return ret;
    }

    private int handleOnlineConnectUser(ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        String mail = "";

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            ExchRpcMessageDefine.ExchOnConnUserMessage message = ExchRpcPacketHandler.toMessage.TransformOnlineConnUserInfo(jsonString);
            mail = message.mail;
            ExchOperation exchOp = new ExchOperation();
            _exchDataCache = exchOp.exchOnConnUser(message);
            addGlobalCache(message.thread_uuid);

            ret = sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_ONLINE_CONNECT_USER.getOpCode(), 0);
        } catch (Exception e){
            logger.error("handle Exchange Online connect user: " + mail +  " failed: " + e.getMessage());
            sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_ONLINE_CONNECT_USER.getOpCode(), ret);
            //force set error
            if (BD_GENERIC_SUCCESS.getCode() == ret){
                ret = BD_GENERIC_ERROR.getCode();
            }
        }

        return ret;
    }

    private int handleGetRootFolder(){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
            String userRootFolder = "";
            ExchOperation exchOp = new ExchOperation();
            userRootFolder = exchOp.getRootFolder(_exchDataCache._ewsClient, _exchDataCache._soapClientCache, _exchDataCache._mail);

            byte[] askInfo = TypeConversion.stringToBytes(userRootFolder);
            ret = buildAndSendAskMsg(ExchRpcMessageDefine.ExchRpcOpcode.EXCH_RPC_OPCODE_GET_ROOT_FOLDER.getOpCode(), askInfo, 0);
        } catch (Exception e){
            logger.error("handle get user: " + _exchDataCache._mail + " root folder failed: " + e.getMessage());
            sendAskHeader(BdRpcMessageDefine.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), M365CommonRpcMessageDefine.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), 1);
            //force set error
            if (BD_GENERIC_SUCCESS.getCode() == ret){
                ret = BD_GENERIC_ERROR.getCode();
            }
        }

        return ret;
    }

    public void destroy(){
        _clientAttachment = null;
        _clientChannel = null;
        _exchDataCache = null;
    }

    public ExchDataCache getExchDataCache(){
        return _exchDataCache;
    }

    public void setExchDataCache(ExchDataCache exchDataCache){
        _exchDataCache = exchDataCache;
    }

    private synchronized boolean addGlobalCache(String threadUuid){
        M365ProxyGlobalVals.ExchConnCache connCache = new M365ProxyGlobalVals.ExchConnCache();
        connCache._organizationAuthParameters = _exchDataCache._organizationAuthParameters;
        connCache._graphClient = _exchDataCache._graphClient;
        connCache._ewsClient = _exchDataCache._ewsClient;
        connCache._mail = _exchDataCache._mail;
        M365ProxyGlobalVals.g_exch_conn_caches.put(threadUuid, connCache);

        return true;
    }

    public static class ExchDataCache{
        public Map<String, String> _organizationAuthParameters;
        public ExchangeService _ewsClient = null;
        public GraphServiceClient<Request> _graphClient = null;
        public List<Map> _soapClientCache = null;
        public String _structContentFile = "";
        public byte[] _mimecontent = null;
        public BufferedReader _soapResponseReader = null;
        public String _mail = "";
    }
}
