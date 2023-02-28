/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Exch_rpc_message_define.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy.m365_rpc_message;

public class Exch_rpc_message_define {

    public enum ExchRpcOpType {
        EXCH_RPC_OP_TYPE_COMMON(0),
        EXCH_RPC_OP_TYPE_MAIL(1),                // mail rpc operation type
        EXCH_RPC_OP_TYPE_APPOINTMENT(2),         // appointment rpc operation type
        EXCH_RPC_OP_TYPE_CONTACT(3),             // contact rpc operation type
        EXCH_RPC_OP_TYPE_TASK(4);                // task rpc operation type

        private int opType = 0;

        private ExchRpcOpType(int value) {
            opType = value;
        }

        private int getCode() {
            return opType;
        }

        public static Exch_rpc_message_define.ExchRpcOpType getOpTypeEnum(int opType) {
            for (Exch_rpc_message_define.ExchRpcOpType exchRpcOpType : Exch_rpc_message_define.ExchRpcOpType.values()) {
                if (exchRpcOpType.getCode() == opType) {
                    return exchRpcOpType;
                }
            }

            return null;
        }
    }

    /**
     * Set common rpc operation of M365, including m365_client_manager,m365_client_transfer
     * number : 200~500
     */
    public enum ExchRpcOpcode{
        /******************************************* exch common type: 201~250 *****************************************/
        EXCH_RPC_OPCODE_GET_ROOT_FOLDER(200),
        /******************************************* mail type: 251~300 *****************************************/
        EXCH_RPC_OPCODE_GET_MAIL_CHILD_FOLDER_BY_GRAPH(251),
        EXCH_RPC_OPCODE_GET_MAIL_CHILD_FOLDER_BY_EWS(252),
        EXCH_RPC_OPCODE_GET_MAIL_LIST_BY_GRAPH(253),
        EXCH_RPC_OPCODE_GET_MAIL_LIST_BY_EWS(254),
        EXCH_RPC_OPCODE_GET_MAIL_INFO_BY_EWS(255),
        EXCH_RPC_OPCODE_GET_MAIL_MIMECONTENT_BY_EWS(256),
        EXCH_RPC_OPCODE_GET_MAIL_XML_DATA_BY_SOAP(257);
        /******************************************* event type: 301~350 *****************************************/


        /******************************************* contact type: 351~400 *****************************************/


        /******************************************* task type: 401~450 *****************************************/

        private int opCode = 0;

        private ExchRpcOpcode(int value) {
            opCode = value;
        }

        private int getOpCode() {
            return opCode;
        }

        public static Exch_rpc_message_define.ExchRpcOpcode getOpCodeEnum(int opCode){
            for (Exch_rpc_message_define.ExchRpcOpcode ExchRpcOpcode : Exch_rpc_message_define.ExchRpcOpcode.values()){
                if (ExchRpcOpcode.getOpCode() == opCode){
                    return  ExchRpcOpcode;
                }
            }

            return null;
        }
    }
}
