/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyError.java: definition of the M365 Error class
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy;

public class M365ProxyError {
    public enum BdErrorCode{
        BD_GENERIC_SUCCESS(0),                                                             //generic success
        BD_GENERIC_ERROR(1),                                                               //generic error
        BD_NOT_INIT_ERROR(2),
        BD_REPEAT_INIT_ERROR(3),				                                             //object repeat init error
        BD_INVALID_PARAM_ERROR(4),				                                         //parameter error
        BD_QUEUE_EMPTY_ERROR(5),				                                             //queue empty
        BD_MEM_FAILED_ERROR(6),				                                             //memory failed
        BD_INTERRUPT_ERROR(7),				                                             //interrupt failed

        BD_NET_TIME_OUT_ERROR(8),				                                             //socket time out
        BD_NET_BIND_ERROR(9),					                                             //socket bind error
        BD_NET_LISTEN_ERROR(10),				                                             //socket listen error
        BD_NET_CONNECT_ERROR(11),				                                             //socket connect error
        BD_NET_RECV_DATA_ERROR(12),				                                         //recv data error
        BD_NET_SEND_DATA_ERROR(13),				                                         //send data error
        BD_NET_RECV_TIMEOUT_ERROR(14),			                                         //receive data timeout
        BD_RPC_NETWORK_ERROR(15),
        BD_NET_PACKET_INVALID_ERROR(16),		                                             //packet invalid

        BD_NET_JSON_KEY_NOT_FOUND_ERROR(17),	                                             //json key not found error
        PARSE_JSON_STRING_ERROR(18),                                                       //parse json to object error
        BD_RPC_MSG_ERROR(19);					                                             //invalid message

        private int errorCode = 0;

        BdErrorCode(int value){
            errorCode = value;
        }

        public int getCode(){
            return errorCode;
        }

        public static BdErrorCode getErrorCodeEnum(int errorCode){
            for (BdErrorCode bdErrorCode : BdErrorCode.values()){
                if (bdErrorCode.getCode() == errorCode){
                    return bdErrorCode;
                }
            }

            return null;
        }
    }

    public enum M365ErrorCode{
        M365_RPC_MSG_ERROR(1000),                                                            //invalid message
        M365_GET_ORGANIZATION_INFO_ERROR(1001),				                               //get Microsoft 365 or Microsoft 365 on-premises organization info error
        M365_GET_USER_LIST_ERROR(1002),                                                      //get user list error
        M365_GET_GROUP_LIST_ERROR(1003),                                                     //get group list error
        M365_CONNECT_USER_BY_GRAPH_ERROR(1004),                                              //use graph api connect user error
        M365_CONNECT_USER_BY_EWS_ERROR(1005),                                                //use ews api connect user error
        M365_GET_USER_ROOT_FOLDER_INFO_ERROR(1006);                                          //get user root folder info error


        private int errorCode = 0;

        M365ErrorCode(int value) {
            errorCode = value;
        }

        public int getCode(){
            return errorCode;
        }

        public static M365ErrorCode getErrorCodeEnum(int errorCode){
            for (M365ErrorCode m365ErrorCode : M365ErrorCode.values()){
                if (m365ErrorCode.getCode() == errorCode){
                    return m365ErrorCode;
                }
            }

            return null;
        }
    }

    public static class ErrorCode{
        private int errorCode;

        public int getErrorCode(){
            return errorCode;
        }

        public void setErrorCode(int code){
            errorCode = code;
        }
    }
}
