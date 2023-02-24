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
import m365_proxy.m365_rpc_client.BdCommonRpcMessageHeader;
import m365_proxy.m365_rpc_client.BdRpcOpType;
import m365_proxy.m365_rpc_client.M365RpcOpcode;
import m365_proxy.m365_rpc_client.M365_rpc_message_define;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class M365_proxy_rpc_server {
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
     * @Description set auto destroy flag
     */
    public void setDestroy(){
        _destroy = true;
    }

    public void waitAndHandleRequest() throws InterruptedException {
        boolean ret = false;
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
                    ret = HandleRpcPublicPacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                    break;
                case BD_RPC_OP_TYPE_PRIVATE:
                    ret = HandleRpcPrivatePacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                    break;
                default:
                    ret = false;
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

        System.out.println("socket finished!!!");
    }

    public boolean HandleRpcPrivatePacket(int privateRpcOpcode, ByteBuffer byteBuffer, long length) throws InterruptedException {
        switch (M365RpcOpcode.getOpCodeEnum(privateRpcOpcode)){
            case M365_RPC_OPCODE_CONNECT_GROUP:
                System.out.println(new String(byteBuffer.array()));
                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                String userJson =  handleGetUser();
                TimeUnit.SECONDS.sleep(500L);
                break;
            default:
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                break;
        }
        return true;
    }

    public boolean HandleRpcPublicPacket(int publicRpcOpcode, ByteBuffer byteBuffer, long length){
        return true;
    }

    private String handleGetUser(){
        M365_proxy_operation m365ProxyOp = new M365_proxy_operation();
        return m365ProxyOp.getUserInfo();
    }


    private ByteBuffer recv(int length){
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);

        while (true){
            try{
                int recvLength = _clientChannel.read(byteBuffer).get(M365_rpc_message_define.M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
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
