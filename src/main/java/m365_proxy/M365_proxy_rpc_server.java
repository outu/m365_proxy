/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_rpc_server.java:waiting and handle rpc server request
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import common.TypeConversion;

import m365_proxy.m365_rpc_message.Bd_rpc_message_define;
import m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdCommonRpcMessageHeader;
import m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdCommonRpcAskMessageHeader;
import m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdRpcOpType;
import m365_proxy.m365_rpc_message.Exch_rpc_message_define.ExchRpcOpType;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365CommonDetectEnvMessage;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365RpcOpType;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365CommonRpcOpcode;
import m365_proxy.m365_rpc_message.M365_common_rpc_packet_handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365_DEFAULT_RPC_TIMEOUT;

public class M365_proxy_rpc_server {
    public static final Logger logger = LoggerFactory.getLogger(M365_proxy_rpc_server.class);
    private AsynchronousSocketChannel _clientChannel;
    private M365_proxy_listen_connection _clientAttachment;

    /**
     * thread manager control child socket thread destroy flag
     */
    private boolean _destroy = false;

    /**
     * @Description  init rpc server
     * @param socketChannel The result type of the I/O operation
     * @param attachment The type of the object attached to the I/O operation
     */
    public void init(AsynchronousSocketChannel socketChannel, M365_proxy_listen_connection attachment){
        _clientChannel = socketChannel;
        _clientAttachment = attachment;
    }

    /**
     *Loop to receive socket messages and process messages
     * @throws InterruptedException
     */
    public void waitAndHandleRequest() throws InterruptedException {
        boolean ret = true;

        BdCommonRpcMessageHeader rpcHeader = new BdCommonRpcMessageHeader();

        while (true){
            //recv header
            ByteBuffer recvByteBuffer = recv(rpcHeader.bd_common_rpc_message_header_size);
            if (recvByteBuffer == null){
                break;
            }

            //parse header
            rpcHeader = parseBdCommonRpcMessageHeader(recvByteBuffer);

            if (rpcHeader.op_type != BdRpcOpType.BD_RPC_OP_TYPE_PUBLIC.ordinal() && rpcHeader.op_type != BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.ordinal()){
                break;
            }

            //recv body
            if(rpcHeader.body_len > 0){
                recvByteBuffer.clear();
                recvByteBuffer = recv((int)rpcHeader.body_len);
                if (recvByteBuffer == null){
                    break;
                }
            }

            switch (BdRpcOpType.getOpTypeEnum(rpcHeader.op_type)){
                case BD_RPC_OP_TYPE_PUBLIC:
                    ret = handleRpcPublicPacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                    break;
                case BD_RPC_OP_TYPE_PRIVATE:
                    ret = handleRpcPrivatePacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                    break;
                default:
                    ret = false;
                    logger.warn("unknown rpc optype.");
                    break;
            }
            if (ret == false){
                break;
            }

            //Make it possible for child threads to exit automatically, and non-main threads to exit forcibly
            if (_destroy){
                break;
            }

            //build and send ask message

        }
    }

    /**
     * handle rpc private packet
     * @param privateRpcOpcode
     * @param byteBuffer
     * @param length
     * @return
     * @throws InterruptedException
     */
    public boolean handleRpcPrivatePacket(int privateRpcOpcode, ByteBuffer byteBuffer, long length) throws InterruptedException
    {
        M365RpcOpType m365RpcOpType = getM365RpcOpType(privateRpcOpcode);
        boolean ret = true;

        switch (m365RpcOpType){
            case M365_RPC_OP_TYPE_COMMON:
                ret = handleRpcM365CommonPacket(privateRpcOpcode, byteBuffer, length);
                break;
            case M365_RPC_OP_TYPE_EXCH:
                ret = handleRpcExchPacket(privateRpcOpcode, byteBuffer, length);
                break;
            default:
                ret = false;
                logger.warn("unknown M365 rpc optype.");
                break;
        }

        return ret;
    }

    /**
     * handle rpc public packet
     * @param publicRpcOpcode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcPublicPacket(int publicRpcOpcode, ByteBuffer byteBuffer, long length){
        return true;
    }

    /**
     * handle rpc m365 common packet
     * @param m365CommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcM365CommonPacket(int m365CommonRpcCode, ByteBuffer byteBuffer, long length){
        boolean ret = true;

        switch (M365CommonRpcOpcode.getOpCodeEnum(m365CommonRpcCode)){
            case M365_COMMON_RPC_OPCODE_DETECT_ENV:
                ret = handleDetectM365Env(byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_USER_LIST:
                ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_GET_GROUP_LIST:
                ret = handleRpcM365CommonPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_CONNECT_USER:
                ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_OPCODE_CONNECT_GROUP:
                ret = handleRpcM365CommonPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            case M365_COMMON_RPC_IS_USER_EXISTS:
                ret = handleRpcExchPacket(m365CommonRpcCode, byteBuffer, length);
                break;
            default:
                ret = false;
                logger.warn("unknown M365 rpc opcode.");
                break;
        }

        return ret;
    }

    /**
     * handle Exchange online&Exchange Server packet
     * @param exchRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchPacket(int exchRpcCode, ByteBuffer byteBuffer, long length){
        boolean ret = true;
        ExchRpcOpType exchRpcOpType = getExchRpcOpType(exchRpcCode);
        logger.debug("handle private->m365 exch packet....");

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
                logger.debug("111111111111111111111111111111111111....");
                ret = handleRpcExchCommonPacket(exchRpcCode, byteBuffer, length);
                break;
        }

        return ret;
    }

    /**
     * handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param mailRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchMailPacket(int mailRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 common-> mail packet....");
        return true;
    }

    /**
     * handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param appointmentRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchAppointmentPacket(int appointmentRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> appointment packet....");
        return true;
    }

    /**
     * handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param contactRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchContactPacket(int contactRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> contact packet....");
        return true;
    }

    /**
     * handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param taskRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchTaskPacket(int taskRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> task packet....");
        return true;
    }

    /**
     * handle mail rpc packet in Exchange Online&Exchange Server with M365
     * @param exchCommonRpcCode
     * @param byteBuffer
     * @param length
     * @return
     */
    public boolean handleRpcExchCommonPacket(int exchCommonRpcCode, ByteBuffer byteBuffer, long length){
        logger.debug("handle private->m365 exch-> common packet....");
        return true;
    }

    /**
     * M365 application classification of packet according to opcode, like Exchange/Sharepoint...
     * @param pubOrPriRpcOpType
     * @return
     */
    private M365RpcOpType getM365RpcOpType(int pubOrPriRpcOpType){
        if (100 <= pubOrPriRpcOpType && pubOrPriRpcOpType < 200){
            return M365RpcOpType.M365_RPC_OP_TYPE_COMMON;
        } else if (200 <= pubOrPriRpcOpType && pubOrPriRpcOpType < 400) {
            return M365RpcOpType.M365_RPC_OP_TYPE_EXCH;
        } else {
            return null;
        }
    }

    /**
     * @deprecated Exchange Online&Exhchange Server type classification of packet according to opcode, like mail|appointment|contact|task
     * @param exchOpcode
     * @return
     */
    private ExchRpcOpType getExchRpcOpType(int exchOpcode){
        if (250 <= exchOpcode && exchOpcode < 300){
            return ExchRpcOpType.EXCH_RPC_OP_TYPE_MAIL;
        } else if (300 <= exchOpcode && exchOpcode < 350) {
            return ExchRpcOpType.EXCH_RPC_OP_TYPE_APPOINTMENT;
        } else if (350 <= exchOpcode && exchOpcode < 400) {
            return ExchRpcOpType.EXCH_RPC_OP_TYPE_CONTACT;
        } else if (400 <= exchOpcode && exchOpcode < 450) {
            return ExchRpcOpType.EXCH_RPC_OP_TYPE_TASK;
        } else {
            return ExchRpcOpType.EXCH_RPC_OP_TYPE_COMMON;
        }
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
            M365CommonDetectEnvMessage message = M365_common_rpc_packet_handler.toMessage.TransformM365CommonDetectEnvInfo(jsonString);

            M365_proxy_operation m365ProxyOp = new M365_proxy_operation();
            String detectM365EnvAskInfo = m365ProxyOp.detectM365Env(message);
            byte[] askInfo = TypeConversion.stringToBytes(detectM365EnvAskInfo);
            buildAndSendAskMsg(M365CommonRpcOpcode.M365_COMMON_RPC_OPCODE_DETECT_ENV.getOpCode(), askInfo);

        } catch (Exception e){
            logger.error("handle detect m365 env failed: " + e.getMessage());
            //send error
            ret = false;
        }

        return ret;
    }


    private ByteBuffer recv(int length){
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);

        while (true){
            try{
                int recvLength = _clientChannel.read(byteBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
                if (recvLength < 0){
                    return null;
                }
                break;
            } catch (ExecutionException | InterruptedException | TimeoutException e){
                return null;
            }
        }

        return byteBuffer;
    }

    private boolean send(byte[] sendPacket){
        ByteBuffer sendBuffer = ByteBuffer.wrap(sendPacket);
        int needSendLength = sendBuffer.capacity();

        while (true){
            try{
                int sendLength = _clientChannel.write(sendBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
                if (sendLength != needSendLength){
                    return false;
                }
            } catch (ExecutionException | InterruptedException | TimeoutException e){
                return false;
            }
        }
    }

    public boolean buildAndSendAskMsg(int opcode, byte[] askData){
        boolean ret = true;

        byte[] askHeader = buildAskHeader(opcode, askData.length + 1);
        byte[] sendPacket = new byte[askHeader.length + askData.length + 1];
        System.arraycopy(askHeader, 0, sendPacket, 0, askHeader.length);
        System.arraycopy(askData, 0, sendPacket, askHeader.length, askData.length);

        ret = send(sendPacket);

        return ret;
    }

    public boolean sendAskMsg(int rpcOpType, int askOpCode, int errorCode){
        boolean ret = true;

        byte[] askHeader  = new byte[24];
        byte[] op_type    = TypeConversion.intToBytes(rpcOpType);
        byte[] opcode     = TypeConversion.intToBytes(askOpCode);
        byte[] reserved   = TypeConversion.intToBytes(0);
        byte[] error_code = TypeConversion.intToBytes(errorCode);
        byte[] body_len   = TypeConversion.longToBytes(0);
        System.arraycopy(op_type, 0, askHeader, 0, op_type.length);
        System.arraycopy(opcode, 0, askHeader, 4, opcode.length);
        System.arraycopy(reserved, 0, askHeader, 8, reserved.length);
        System.arraycopy(error_code, 0, askHeader, 12, error_code.length);
        System.arraycopy(body_len, 0, askHeader, 16, body_len.length);

        ret = send(askHeader);

        return ret;
    }

    public byte[] buildAskHeader(int askOpCode, int bodyLength){
        byte[] askHeader  = new byte[24];
        byte[] op_type    = TypeConversion.intToBytes(BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode());
        byte[] opcode     = TypeConversion.intToBytes(askOpCode);
        byte[] reserved   = TypeConversion.intToBytes(0);
        byte[] error_code = TypeConversion.intToBytes(0);
        byte[] body_len   = TypeConversion.longToBytes(bodyLength);
        System.arraycopy(op_type, 0, askHeader, 0, op_type.length);
        System.arraycopy(opcode, 0, askHeader, 4, opcode.length);
        System.arraycopy(reserved, 0, askHeader, 8, reserved.length);
        System.arraycopy(error_code, 0, askHeader, 12, error_code.length);
        System.arraycopy(body_len, 0, askHeader, 16, body_len.length);

        return askHeader;
    }

    private BdCommonRpcMessageHeader parseBdCommonRpcMessageHeader(ByteBuffer byteBuffer){
        byte[] op_type       = new byte[2];
        byte[] need_response = new byte[2];
        byte[] opcode        = new byte[4];
        byte[] body_len      = new byte[8];

        System.arraycopy(byteBuffer.array(), 0, op_type, 0, op_type.length);
        System.arraycopy(byteBuffer.array(), 2, need_response, 0, need_response.length);
        System.arraycopy(byteBuffer.array(), 4, opcode, 0, opcode.length);
        System.arraycopy(byteBuffer.array(), 8, body_len, 0, body_len.length);

        BdCommonRpcMessageHeader bdCommonRpcMessageHeader = new BdCommonRpcMessageHeader();
        bdCommonRpcMessageHeader.op_type = TypeConversion.bytesToInt(op_type);
        bdCommonRpcMessageHeader.need_response = TypeConversion.bytesToInt(need_response);
        bdCommonRpcMessageHeader.opcode = TypeConversion.bytesToLong(opcode);
        bdCommonRpcMessageHeader.body_len = TypeConversion.bytesToLong(body_len);

        return bdCommonRpcMessageHeader;
    }
}
