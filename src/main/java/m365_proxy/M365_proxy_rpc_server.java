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
import m365_proxy.m365_rpc_server_handler.Exch_rpc_server_handler.ExchDataCache;

import m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdCommonRpcMessageHeader;
import m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdRpcOpType;
import m365_proxy.m365_rpc_message.M365_common_rpc_message_define.M365RpcOpType;
import m365_proxy.m365_rpc_server_handler.Exch_rpc_server_handler;
import m365_proxy.m365_rpc_server_handler.M365_common_rpc_server_handler;
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
    protected AsynchronousSocketChannel _clientChannel;
    protected M365_proxy_listen_connection _clientAttachment;

    protected ExchDataCache _exchDataCache = null;

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
     *@Description Loop to receive socket messages and process messages
     */
    public void waitAndHandleRequest() {
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
        }
    }

    /**
     * @Description handle rpc private packet
     * @param privateRpcOpcode
     * @param byteBuffer
     * @param length
     * @return
     * @throws InterruptedException
     */
    private boolean handleRpcPrivatePacket(int privateRpcOpcode, ByteBuffer byteBuffer, long length)
    {
        M365RpcOpType m365RpcOpType = getM365RpcOpType(privateRpcOpcode);
        boolean ret = true;

        switch (m365RpcOpType){
            case M365_RPC_OP_TYPE_COMMON:
                M365_common_rpc_server_handler commonRpcHandler = new M365_common_rpc_server_handler();
                commonRpcHandler.init(_clientChannel, _clientAttachment);
                ret = commonRpcHandler.handleRpcM365CommonPacket(privateRpcOpcode, byteBuffer, length);
                commonRpcHandler.destroy();
                break;
            case M365_RPC_OP_TYPE_EXCH:
                Exch_rpc_server_handler exchRpcHandler = new Exch_rpc_server_handler();
                exchRpcHandler.init(_clientChannel, _clientAttachment);

                if (_exchDataCache != null){
                    exchRpcHandler.setExchDataCache(_exchDataCache);
                }

                ret = exchRpcHandler.handleRpcExchPacket(privateRpcOpcode, byteBuffer, length);

                if (_exchDataCache == null && exchRpcHandler.getExchDataCache() != null){
                    _exchDataCache = exchRpcHandler.getExchDataCache();
                }
                exchRpcHandler.destroy();
                break;
            default:
                ret = false;
                logger.warn("unknown M365 rpc optype.");
                break;
        }

        return ret;
    }

    /**
     * @Description handle rpc public packet
     * @param publicRpcOpcode
     * @param byteBuffer
     * @param length
     * @return
     */
    private boolean handleRpcPublicPacket(int publicRpcOpcode, ByteBuffer byteBuffer, long length){
        return true;
    }

    /**
     * @Description M365 application classification of packet according to opcode, like Exchange/Sharepoint...
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

    protected ByteBuffer recv(int length){
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);

        try{
            int recvLength = _clientChannel.read(byteBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
            if (recvLength < 0){
                return null;
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e){
            return null;
        }

        return byteBuffer;
    }

    protected boolean send(byte[] sendPacket){
        boolean ret = true;

        ByteBuffer sendBuffer = ByteBuffer.wrap(sendPacket);
        int needSendLength = sendBuffer.capacity();

        try{
            int sendLength = _clientChannel.write(sendBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
            if (sendLength != needSendLength){
                ret = false;
            }
        } catch (ExecutionException | InterruptedException | TimeoutException e){
            ret = false;
        }

        return ret;
    }

    protected boolean buildAndSendAskMsg(int opcode, byte[] askData){
        boolean ret = true;

        byte[] askHeader = buildAskHeader(opcode, askData.length + 1);
        byte[] sendPacket = new byte[askHeader.length + askData.length + 1];
        System.arraycopy(askHeader, 0, sendPacket, 0, askHeader.length);
        System.arraycopy(askData, 0, sendPacket, askHeader.length, askData.length);

        ret = send(sendPacket);

        return ret;
    }

    protected boolean sendAskMsg(int rpcOpType, int askOpCode, int errorCode){
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

    protected byte[] buildAskHeader(int askOpCode, int bodyLength){
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
