/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Exch_rpc_server_handler.java: Exchange Online&Exchange Server rpc server handler class
 * Author		:	yangjunjie
 * Date			:	2023/03/01
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_server_handler;

import apis.BaseUtil;
import com.microsoft.graph.requests.GraphServiceClient;
import common.TypeConversion;
import m365_proxy.M365_proxy_global_vals;
import m365_proxy.M365_proxy_rpc_server;
import m365_proxy.m365_proxy_operation.Exch_operation;
import m365_proxy.m365_proxy_operation.M365_common_operation;
import m365_proxy.m365_rpc_message.*;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.ByteBuffer;
import java.util.Map;

public class Exch_rpc_server_handler extends M365_proxy_rpc_server {
    public static final Logger logger = LoggerFactory.getLogger(Exch_rpc_server_handler.class);
    /**
     * @Description handle Exchange online&Exchange Server packet
     * @param exchRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchPacket(int exchRpcCode, ByteBuffer byteBuffer, long length){
        boolean ret = true;
        Exch_rpc_message_define.ExchRpcOpType exchRpcOpType = getExchRpcOpType(exchRpcCode);

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
    private Exch_rpc_message_define.ExchRpcOpType getExchRpcOpType(int exchOpcode){
        if (250 <= exchOpcode && exchOpcode < 300){
            return Exch_rpc_message_define.ExchRpcOpType.EXCH_RPC_OP_TYPE_MAIL;
        } else if (300 <= exchOpcode && exchOpcode < 350) {
            return Exch_rpc_message_define.ExchRpcOpType.EXCH_RPC_OP_TYPE_APPOINTMENT;
        } else if (350 <= exchOpcode && exchOpcode < 400) {
            return Exch_rpc_message_define.ExchRpcOpType.EXCH_RPC_OP_TYPE_CONTACT;
        } else if (400 <= exchOpcode && exchOpcode < 450) {
            return Exch_rpc_message_define.ExchRpcOpType.EXCH_RPC_OP_TYPE_TASK;
        } else {
            return Exch_rpc_message_define.ExchRpcOpType.EXCH_RPC_OP_TYPE_COMMON;
        }
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param mailRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcExchMailPacket(int mailRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 common-> mail packet....");
        return true;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param appointmentRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcExchAppointmentPacket(int appointmentRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> appointment packet....");
        return true;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param contactRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcExchContactPacket(int contactRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> contact packet....");
        return true;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param taskRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcExchTaskPacket(int taskRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> task packet....");
        return true;
    }

    /**
     * @Description handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param exchCommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcExchCommonPacket(int exchCommonRpcCode, ByteBuffer byteBuffer, long length){
        boolean ret = true;

        switch (Exch_rpc_message_define.ExchRpcOpcode.getOpCodeEnum(exchCommonRpcCode)){
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
                ret = false;
                logger.warn("unknown M365 exch rpc opcode.");
                break;
        }

        return ret;
    }

    private boolean handleDetectEnv(ByteBuffer byteBuffer, long length){
        boolean ret = true;

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            Exch_rpc_message_define.ExchSerDetectEnvMessage message = Exch_rpc_packet_handler.toMessage.TransformDetectEnvInfo(jsonString);

            Exch_operation exchOp = new Exch_operation();
            String detectEnvAskInfo = exchOp.detectEnv(message);
            byte[] askInfo = TypeConversion.stringToBytes(detectEnvAskInfo);
            buildAndSendAskMsg(M365_common_rpc_message_define.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), askInfo);

        } catch (Exception e){
            logger.error("handle detect m365 env failed: " + e.getMessage());
            //send error
            ret = false;
        }

        return ret;
    }

    private boolean handleServiceConnectUser(ByteBuffer byteBuffer, long length){
        boolean ret = true;
        String mail = "";

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            Exch_rpc_message_define.ExchSerConnUserMessage message = Exch_rpc_packet_handler.toMessage.TransformServerConnUserInfo(jsonString);
            mail = message.mail;
            Exch_operation exchOp = new Exch_operation();
            _exchDataCache = exchOp.exchSerConnUser(message);
            addGlobalCache(message.thread_uuid);

            sendAskMsg(Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), Exch_rpc_message_define.ExchRpcOpcode.EXCH_RPC_OPCODE_SERVICE_CONNECT_USER.getOpCode(), 0);
        } catch (Exception e){
            logger.error("handle Exchange Server connect user: " + mail +  " failed: " + e.getMessage());
            sendAskMsg(Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), Exch_rpc_message_define.ExchRpcOpcode.EXCH_RPC_OPCODE_SERVICE_CONNECT_USER.getOpCode(), 1);
            ret = false;
        }

        return ret;
    }

    private boolean handleOnlineConnectUser(ByteBuffer byteBuffer, long length){
        boolean ret = true;
        String mail = "";

        try {
            String jsonString = TypeConversion.byteBufferToString(byteBuffer);
            Exch_rpc_message_define.ExchOnConnUserMessage message = Exch_rpc_packet_handler.toMessage.TransformOnlineConnUserInfo(jsonString);
            mail = message.mail;
            Exch_operation exchOp = new Exch_operation();
            _exchDataCache = exchOp.exchOnConnUser(message);
            addGlobalCache(message.thread_uuid);

            sendAskMsg(Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), Exch_rpc_message_define.ExchRpcOpcode.EXCH_RPC_OPCODE_ONLINE_CONNECT_USER.getOpCode(), 0);
        } catch (Exception e){
            logger.error("handle Exchange Online connect user: " + mail +  " failed: " + e.getMessage());
            sendAskMsg(Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), Exch_rpc_message_define.ExchRpcOpcode.EXCH_RPC_OPCODE_ONLINE_CONNECT_USER.getOpCode(), 1);
            ret = false;
        }

        return ret;
    }

    private boolean handleGetRootFolder(){
        boolean ret = true;

        try {
            String userRootFolder = "";
            Exch_operation exchOp = new Exch_operation();
            if (Integer.parseInt(_exchDataCache._organizationAuthParameters.get("region")) != BaseUtil.RegionEnum.LOCAL.getCode()){
                userRootFolder = exchOp.getRootFolder(_exchDataCache._ewsClient, _exchDataCache._graphClient, _exchDataCache._mail);
            } else {
                userRootFolder = exchOp.getRootFolder(_exchDataCache._ewsClient);
            }
            byte[] askInfo = TypeConversion.stringToBytes(userRootFolder);
            buildAndSendAskMsg(Exch_rpc_message_define.ExchRpcOpcode.EXCH_RPC_OPCODE_GET_ROOT_FOLDER.getOpCode(), askInfo);
        } catch (Exception e){
            logger.error("handle get user: " + _exchDataCache._mail + " root folder failed: " + e.getMessage());
            sendAskMsg(Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode(), M365_common_rpc_message_define.M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), 1);
            ret = false;
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
        M365_proxy_global_vals.ExchConnCache connCache = new M365_proxy_global_vals.ExchConnCache();
        connCache._organizationAuthParameters = _exchDataCache._organizationAuthParameters;
        connCache._graphClient = _exchDataCache._graphClient;
        connCache._ewsClient = _exchDataCache._ewsClient;
        connCache._mail = _exchDataCache._mail;
        M365_proxy_global_vals.g_exch_conn_caches.put(threadUuid, connCache);

        return true;
    }

    public static class ExchDataCache{
        public Map<String, String> _organizationAuthParameters;
        public ExchangeService _ewsClient = null;
        public GraphServiceClient<Request> _graphClient = null;
        public String _structContentFile = "";
        public byte[] _mimecontent = null;
        public BufferedReader _soapResponseReader = null;
        public String _mail = "";
    }
}
