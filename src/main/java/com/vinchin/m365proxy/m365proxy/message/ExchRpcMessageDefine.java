/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	ExchRpcMessageDefine.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy.message;

public class ExchRpcMessageDefine {

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

        public static ExchRpcOpType getOpTypeEnum(int opType) {
            for (ExchRpcOpType exchRpcOpType : ExchRpcOpType.values()) {
                if (exchRpcOpType.getCode() == opType) {
                    return exchRpcOpType;
                }
            }

            return EXCH_RPC_OP_TYPE_COMMON;
        }
    }

    /**
     * Set common rpc operation of M365, including m365_client_manager,m365_client_transfer
     * number : 200~500
     */
    public enum ExchRpcOpcode{
        /******************************************* exch common type: 201~250 *****************************************/
        EXCH_RPC_OPCODE_DETECT_ENV(200),
        EXCH_RPC_OPCODE_SERVICE_CONNECT_USER(201),
        EXCH_RPC_OPCODE_ONLINE_CONNECT_USER(202),
        EXCH_RPC_OPCODE_GET_ROOT_FOLDER(203),
        /******************************************* mail type: 251~300 *****************************************/
        EXCH_RPC_OPCODE_GET_MAIL_CHILD_FOLDER_BY_GRAPH(251),
        EXCH_RPC_OPCODE_GET_MAIL_CHILD_FOLDER_BY_EWS(252),
        EXCH_RPC_OPCODE_GET_MAIL_LIST_BY_GRAPH(253),
        EXCH_RPC_OPCODE_GET_MAIL_LIST_BY_EWS(254),
        EXCH_RPC_OPCODE_GET_MAIL_INFO_BY_EWS(255),
        EXCH_RPC_OPCODE_GET_MAIL_MIMECONTENT_BY_EWS(256),
        EXCH_RPC_OPCODE_GET_MAIL_XML_DATA_BY_SOAP(257),
        /******************************************* event type: 301~350 *****************************************/


        /******************************************* contact type: 351~400 *****************************************/


        /******************************************* task type: 401~450 *****************************************/


        /******************************************* unknown type: 451~500 *****************************************/
        EXCH_RPC_OPCODE_UNKNOWN(451);

        private int opCode = 0;

        private ExchRpcOpcode(int value) {
            opCode = value;
        }

        public int getOpCode() {
            return opCode;
        }

        public static ExchRpcOpcode getOpCodeEnum(int opCode){
            for (ExchRpcMessageDefine.ExchRpcOpcode ExchRpcOpcode : ExchRpcOpcode.values()){
                if (ExchRpcOpcode.getOpCode() == opCode){
                    return  ExchRpcOpcode;
                }
            }

            return EXCH_RPC_OPCODE_UNKNOWN;
        }
    }

    public static class ExchSerDetectEnvMessage{
        public int region = 0;
        public String username = "";
        public String password = "";
        public String domain = "";
    }

    public static class ExchSerConnUserMessage{
        public int region = 0;
        public String username = "";
        public String password = "";
        public String domain = "";
        public String mail = "";
        public String thread_uuid = "";
    }

    public static class ExchOnConnUserMessage{
        public int region = 0;
        public String tenant_Uuid = "";
        public String app_uuid = "";
        public String app_secret = "";
        public String username = "";
        public String mail = "";
        public String thread_uuid = "";
        public M365CommonRpcMessageDefine.M365AuthAppsInfo auth_apps = null;
        public M365CommonRpcMessageDefine.M365AzureADAppCertInfo app_cert_info = null;
    }
}
