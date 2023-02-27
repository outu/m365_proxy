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

public class M365_common_rpc_message_define {
    public final static long M365_DEFAULT_RPC_TIMEOUT = 600;
    public final static long M365_DEFAULT_RPC_RETRY_TIMEOUT = 1800;

    /**
     * Microsoft365 submodule Rpc optype: Common/Exchange/sharepoint...
     */
    public enum M365RpcOpType {
        M365_RPC_OP_TYPE_COMMON(1),       // common rpc operation type
        M365_RPC_OP_TYPE_EXCH(2);         // Exchange Online&Exchange Server rpc operation type

        private int opType = 0;

        private M365RpcOpType(int value) {
            opType = value;
        }

        private int getCode() {
            return opType;
        }

        public static M365_common_rpc_message_define.M365RpcOpType getOpTypeEnum(int opType) {
            for (M365_common_rpc_message_define.M365RpcOpType m365RpcOpType : M365_common_rpc_message_define.M365RpcOpType.values()) {
                if (m365RpcOpType.getCode() == opType) {
                    return m365RpcOpType;
                }
            }

            return null;
        }
    }

    /**
     * Set common rpc operation of M365, including m365_client_manager,m365_client_transfer
     */
    public static enum M365CommonRpcOpcode{
        M365_COMMON_RPC_OPCODE_DETECT_ENV(100),
        M365_COMMON_RPC_OPCODE_GET_USER_LIST(101),
        M365_COMMON_RPC_OPCODE_GET_GROUP_LIST(102),
        M365_COMMON_RPC_OPCODE_CONNECT_USER(103),
        M365_COMMON_RPC_OPCODE_CONNECT_GROUP(104),
        M365_COMMON_RPC_IS_USER_EXISTS(105);

        private int opCode = 0;

        private M365CommonRpcOpcode(int value) {
            opCode = value;
        }

        private int getOpCode() {
            return opCode;
        }

        public static M365CommonRpcOpcode getOpCodeEnum(int opCode){
            for (M365CommonRpcOpcode m365CommonRpcOpcode : M365CommonRpcOpcode.values()){
                if (m365CommonRpcOpcode.getOpCode() == opCode){
                    return  m365CommonRpcOpcode;
                }
            }

            return null;
        }
    }

    public static class M365CommonGetUserMessage{
        int region;
        public M365CommonGetUserMessage(){
            region = 0;
        }
    }
}



