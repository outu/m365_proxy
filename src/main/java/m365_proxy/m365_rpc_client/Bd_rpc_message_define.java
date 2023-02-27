/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Bd_rpc_message_define.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/24
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_client;

public class Bd_rpc_message_define {
    public enum BdRpcOpType {
        BD_RPC_OP_TYPE_UNKNOWN(0),      // rpc operation type unknown
        BD_RPC_OP_TYPE_PUBLIC(1),       // public rpc operation type
        BD_RPC_OP_TYPE_PRIVATE(2);      // public rpc operation type

        private int opType = 0;

        private BdRpcOpType(int value) {
            opType = value;
        }

        private int getCode() {
            return opType;
        }

        public static BdRpcOpType getOpTypeEnum(int opType) {
            for (BdRpcOpType bdRpcOpType : BdRpcOpType.values()) {
                if (bdRpcOpType.getCode() == opType) {
                    return bdRpcOpType;
                }
            }

            return null;
        }
    }

    public static class BdCommonRpcMessageHeader{
        public int bd_common_rpc_message_header_size;
        public int op_type;
        public int need_response;
        public long opcode;
        public long body_len;

        public BdCommonRpcMessageHeader(){
            bd_common_rpc_message_header_size = 16;
            op_type = 0;
            need_response = 0;
            opcode = 0;
            body_len = 0;
        }
    }
}





