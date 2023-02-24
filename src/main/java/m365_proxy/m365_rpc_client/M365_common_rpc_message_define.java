/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_rpc_message_define.java: definition rpc message
 *                  c/c++      type to      java      bytes
 *                  uint8_t                 short       1
 *                  uint16_t                 int        2
 *                  uint32_t                long        4
 *                  uint64_t                long        8
 *                  Java has no unsigned type, uint64_t There will be one missing when it turns to long,
 *                  but the data packet will not exceed the total length of long
 *
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_client;

/**
 *
 */

public class M365_rpc_message_define {
    public static long M365_DEFAULT_RPC_TIMEOUT = 600;
    public static long M365_DEFAULT_RPC_RETYR_TIMEOUT = 1800;

}

enum BdRpcOpType{
    BD_RPC_OP_TYPE_UNKNOWN(0),      // rpc operation type unknown
    BD_RPC_OP_TYPE_PUBLIC(1),       // public rpc operation type
    BD_RPC_OP_TYPE_PRIVATE(2);    // public rpc operation type

    private int opType = 0;

    private BdRpcOpType(int value) {
        opType = value;
    }

    private int getCode(){
        return opType;
    }

    public static BdRpcOpType getOpTypeEnum(int opType){
        for (BdRpcOpType bdRpcOpType : BdRpcOpType.values()){
            if (bdRpcOpType.getCode() == opType){
                return bdRpcOpType;
            }
        }

        return null;
    }
}

/**
 * Rpc execution opcode set, including m365_client_manager,m365_client_transfer,m365_proxy
 */
enum M365RpcOpcode{
    M365_RPC_OPCODE_DETECT_ENV(100),
    M365_RPC_OPCODE_GET_USER_LIST(102),
    M365_RPC_OPCODE_GET_GROUP_LIST(103),
    /**
     * EXCHANGE ONLINE&EXCHANGE SERVER RPC OPCODE
     */
    M365_RPC_OPCODE_CONNECT_USER(200),
    M365_RPC_OPCODE_CONNECT_GROUP(201),
    M365_RPC_OPCODE_GET_USER_FOLDER_INFO(202);


    private int opCode = 0;

    private M365RpcOpcode(int value) {
        opCode = value;
    }

    private int getOpCode() {
        return opCode;
    }

    public static M365RpcOpcode getOpCodeEnum(int opCode){
        for (M365RpcOpcode m365RpcOpcode : M365RpcOpcode.values()){
            if (m365RpcOpcode.getOpCode() == opCode){
                return  m365RpcOpcode;
            }
        }

        return null;
    }
}

class BdCommonRpcMessageHeader{
    public static int bd_common_rpc_message_header_size = 16;
    public int op_type;
    public int need_response;
    public long opcode;
    public long body_len;
}

class M365ProxyGetUserMessage{
    int region;
    public M365ProxyGetUserMessage(){
        region = 0;
    }
}



