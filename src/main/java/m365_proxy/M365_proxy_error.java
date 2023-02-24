/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_proxy_error.java: definition of the M365 Error class
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

public class M365_proxy_error {
}

enum M365ErrorCode{
    M365_GET_ORGANIZATION_INFO_ERROR(1000),				                               //get Microsoft 365 or Microsoft 365 on-premises organization info error
    M365_GET_USER_LIST_ERROR(1001),                                                      //get user list error
    M365_GET_GROUP_LIST_ERROR(1002),                                                     //get group list error
    M365_CONNECT_USER_BY_GRAPH_ERROR(1003),                                              //use graph api connect user error
    M365_CONNECT_USER_BY_EWS_ERROR(1004),                                                //use ews api connect user error
    M365_GET_USER_ROOT_FOLDER_INFO_ERROR(1005);                                          //get user root folder info error


    private int errorCode = 0;

    private M365ErrorCode(int value) {
        errorCode = value;
    }

    private int getCode(){
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
