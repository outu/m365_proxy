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

import static m365_proxy.M365_proxy_error.BdErrorCode.*;
import static m365_proxy.M365_proxy_error.M365ErrorCode.M365_RPC_MSG_ERROR;
import static m365_proxy.m365_rpc_message.Bd_rpc_message_define.BdRpcOpType.BD_RPC_OP_TYPE_PUBLIC;
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
        int ret = BD_GENERIC_SUCCESS.getCode();

        BdCommonRpcMessageHeader rpcHeader = new BdCommonRpcMessageHeader();
        M365_proxy_error.ErrorCode errCode = new M365_proxy_error.ErrorCode();

        while (true){
            //recv header
            ByteBuffer recvByteBuffer = recv(rpcHeader.bd_common_rpc_message_header_size, errCode);
            if (recvByteBuffer == null){
                logger.error("recv packet header error, error code: " + errCode.getErrorCode());
                break;
            }

            //parse header
            rpcHeader = parseBdCommonRpcMessageHeader(recvByteBuffer);
            if (rpcHeader == null){
                logger.error("parse packet header error.");
                break;
            }

            if (rpcHeader.op_type != BD_RPC_OP_TYPE_PUBLIC.getCode() && rpcHeader.op_type != BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode()){
                ret = BD_NET_PACKET_INVALID_ERROR.getCode();
                logger.error("recive invalid packet header, error code: " + ret);
                break;
            }

            //recv body
            if(rpcHeader.body_len > 0){
                recvByteBuffer.clear();
                recvByteBuffer = recv((int)rpcHeader.body_len, errCode);
                if (recvByteBuffer == null){
                    logger.error("recv packet body error, error code: " + errCode.getErrorCode());
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
                    logger.error("unknown rpc optype.");
                    ret = sendAskHeader(BD_RPC_OP_TYPE_PUBLIC.getCode(), (int)rpcHeader.opcode, ret);
                    //force set error
                    if (BD_GENERIC_SUCCESS.getCode() == ret){
                        ret = BD_RPC_MSG_ERROR.getCode();
                    }
                    break;
            }

            if (BD_RPC_NETWORK_ERROR.getCode() == ret){
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
     */
    private int handleRpcPrivatePacket(int privateRpcOpcode, ByteBuffer byteBuffer, long length)
    {
        M365RpcOpType m365RpcOpType = getM365RpcOpType(privateRpcOpcode);
        int ret = BD_GENERIC_SUCCESS.getCode();

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
                ret = M365_RPC_MSG_ERROR.getCode();
                logger.error("unknown M365 rpc optype.");
                ret = sendAskHeader(BD_RPC_OP_TYPE_PUBLIC.getCode(), privateRpcOpcode, ret);
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
    private int handleRpcPublicPacket(int publicRpcOpcode, ByteBuffer byteBuffer, long length){
        int ret = BD_GENERIC_SUCCESS.getCode();
        return ret;
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
            return M365RpcOpType.M365_RPC_OP_TYPE_UNKNOWN;
        }
    }

    protected ByteBuffer recv(int length, M365_proxy_error.ErrorCode errorCode){
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);

        try{
            int recvLength = _clientChannel.read(byteBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
            if (recvLength < 0){
                errorCode.setErrorCode(BD_RPC_NETWORK_ERROR.getCode());
                return null;
            }
        } catch (ExecutionException e){
            errorCode.setErrorCode(BD_GENERIC_ERROR.getCode());
            return null;
        } catch (InterruptedException e){
            errorCode.setErrorCode(BD_INTERRUPT_ERROR.getCode());
            return null;
        } catch (TimeoutException e){
            errorCode.setErrorCode(BD_NET_TIME_OUT_ERROR.getCode());
            return null;
        }

        return byteBuffer;
    }

    protected int send(byte[] sendPacket){
        int ret = BD_GENERIC_SUCCESS.getCode();
        ByteBuffer sendBuffer = ByteBuffer.wrap(sendPacket);
        int needSendLength = sendBuffer.capacity();

        try{
            int sendLength = _clientChannel.write(sendBuffer).get(M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
            if (sendLength != needSendLength){
                ret = BD_NET_SEND_DATA_ERROR.getCode();
            }
        } catch (ExecutionException e){
            ret = BD_GENERIC_ERROR.getCode();
        } catch (InterruptedException e){
            ret = BD_INTERRUPT_ERROR.getCode();
        } catch (TimeoutException e){
            ret = BD_NET_TIME_OUT_ERROR.getCode();
        }

        return ret;
    }

    protected int buildAndSendAskMsg(int opcode, byte[] askData, int reserved){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try{
            byte[] askHeader = buildAskHeader(opcode, askData.length + 1, reserved);

            if (askHeader == null){
                ret = BD_GENERIC_ERROR.getCode();
            } else {
                byte[] sendPacket = new byte[askHeader.length + askData.length + 1];
                System.arraycopy(askHeader, 0, sendPacket, 0, askHeader.length);
                System.arraycopy(askData, 0, sendPacket, askHeader.length, askData.length);
                ret = send(sendPacket);
            }
        } catch (Exception e){
            ret = BD_MEM_FAILED_ERROR.getCode();
            logger.error("build and send ask msg error, opcode: " + opcode + " , error message: " + e.getMessage());
        }

        if (BD_GENERIC_SUCCESS.getCode() != ret){
            logger.error("send ask header error, opcode: " + opcode + " , error code: " + ret);
            ret = BD_RPC_NETWORK_ERROR.getCode();;
        }

        return ret;
    }

    protected int sendAskHeader(int rpcOpType, int askOpCode, int errorCode){
        int ret = BD_GENERIC_SUCCESS.getCode();

        try {
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
        } catch (Exception e){
            ret = BD_MEM_FAILED_ERROR.getCode();
            logger.error("send ask header error, opcode: " + rpcOpType + " , error message: " + e.getMessage());
            return ret;
        }

        if (BD_GENERIC_SUCCESS.getCode() != ret){
            logger.error("send ask header error, opcode: " + rpcOpType + " , error code: " + ret);
            ret = BD_RPC_NETWORK_ERROR.getCode();;
        }

        return ret;
    }

    protected byte[] buildAskHeader(int askOpCode, int bodyLength, int askReserved){
       byte[] askHeader;

       try{
           askHeader  = new byte[24];
           byte[] op_type    = TypeConversion.intToBytes(BdRpcOpType.BD_RPC_OP_TYPE_PRIVATE.getCode());
           byte[] opcode     = TypeConversion.intToBytes(askOpCode);
           byte[] reserved   = TypeConversion.intToBytes(askReserved);
           byte[] error_code = TypeConversion.intToBytes(BD_GENERIC_SUCCESS.getCode());
           byte[] body_len   = TypeConversion.longToBytes(bodyLength);
           System.arraycopy(op_type, 0, askHeader, 0, op_type.length);
           System.arraycopy(opcode, 0, askHeader, 4, opcode.length);
           System.arraycopy(reserved, 0, askHeader, 8, reserved.length);
           System.arraycopy(error_code, 0, askHeader, 12, error_code.length);
           System.arraycopy(body_len, 0, askHeader, 16, body_len.length);
       } catch (Exception e){
           askHeader = null;
           logger.error("build ask header error, opcode: " + askOpCode + " , error message: " + e.getMessage());
       }

        return askHeader;
    }

    private BdCommonRpcMessageHeader parseBdCommonRpcMessageHeader(ByteBuffer byteBuffer){
        BdCommonRpcMessageHeader bdCommonRpcMessageHeader;

        try{
            byte[] op_type       = new byte[2];
            byte[] need_response = new byte[2];
            byte[] opcode        = new byte[4];
            byte[] body_len      = new byte[8];

            System.arraycopy(byteBuffer.array(), 0, op_type, 0, op_type.length);
            System.arraycopy(byteBuffer.array(), 2, need_response, 0, need_response.length);
            System.arraycopy(byteBuffer.array(), 4, opcode, 0, opcode.length);
            System.arraycopy(byteBuffer.array(), 8, body_len, 0, body_len.length);

            bdCommonRpcMessageHeader = new BdCommonRpcMessageHeader();
            bdCommonRpcMessageHeader.op_type = TypeConversion.bytesToInt(op_type);
            bdCommonRpcMessageHeader.need_response = TypeConversion.bytesToInt(need_response);
            bdCommonRpcMessageHeader.opcode = TypeConversion.bytesToLong(opcode);
            bdCommonRpcMessageHeader.body_len = TypeConversion.bytesToLong(body_len);
        } catch (Exception e){
            bdCommonRpcMessageHeader = null;
            logger.error("parse rpc header error, error message: " + e.getMessage());
        }

        return bdCommonRpcMessageHeader;
    }
}
