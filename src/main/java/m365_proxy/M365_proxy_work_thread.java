/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_work_thread.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import common.TypeConversion;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class M365_proxy_work_thread implements CompletionHandler<AsynchronousSocketChannel, M365_proxy_listen_connection> {
    private AsynchronousSocketChannel clientChannel;

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, M365_proxy_listen_connection attachment) {
        //TODO 移动到M365_proxy_rpc_server类中和c++代码保持一致
        boolean ret = true;
        ByteBuffer sendByteBuffer;
        attachment.getServerChannel().accept(attachment, this);
        clientChannel = socketChannel;
        M365_proxy_rpc_server rpcServer = new M365_proxy_rpc_server();

        while (true){
            try {
                //recv header
                ByteBuffer recvByteBuffer = recv(BdCommonRpcMessageHeader.bd_common_rpc_message_header_size);
                if (recvByteBuffer == null){
                    break;
                }
                //parse header
                BdCommonRpcMessageHeader rpcHeader = parseBdCommonRpcMessageHeader(recvByteBuffer);

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
                        sendByteBuffer = rpcServer.HandleRpcPublicPacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                        break;
                    case BD_RPC_OP_TYPE_PRIVATE:
                        sendByteBuffer = rpcServer.HandleRpcPrivatePacket((int)rpcHeader.opcode, recvByteBuffer, rpcHeader.body_len);
                        break;
                    default:
                        sendByteBuffer = null;
                        break;
                }
                if (sendByteBuffer == null){
                    break;
                }

                //build and send ask message
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void failed(Throwable exc, M365_proxy_listen_connection attachment) {

    }

    private ByteBuffer recv(int length) throws ExecutionException, InterruptedException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);

        while (true){
            try{
                clientChannel.read(byteBuffer).get(M365_rpc_message_define.M365_DEFAULT_RPC_TIMEOUT, TimeUnit.SECONDS);
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
