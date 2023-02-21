/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_rpc_server.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import java.nio.ByteBuffer;

public class M365_proxy_rpc_server {
    public ByteBuffer HandleRpcPrivatePacket(int privateRpcOpcode, ByteBuffer byteBuffer, long length){
        switch (M365RpcOpcode.getOpCodeEnum(privateRpcOpcode)){
            case M365_RPC_OPCODE_PROXY_GET_USER:

                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                return handleGetUser();
            default:
                System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                break;
        }
        return null;
    }

    public ByteBuffer HandleRpcPublicPacket(int publicRpcOpcode, ByteBuffer byteBuffer, long length){
        return null;
    }

    private ByteBuffer handleGetUser(){
        M365_proxy_operation m365ProxyOp = new M365_proxy_operation();
        m365ProxyOp.getUserInfo();
        //构建消息bytebuffer
        return null;
    }
}
