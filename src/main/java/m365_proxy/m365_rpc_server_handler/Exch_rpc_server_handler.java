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

import m365_proxy.M365_proxy_rpc_server;
import m365_proxy.m365_rpc_message.Exch_rpc_message_define;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

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
        logger.debug("handle private->m365 exch-> common packet....");
        return true;
    }

    public void destroy(){
        _clientAttachment = null;
        _clientChannel = null;
    }
}
