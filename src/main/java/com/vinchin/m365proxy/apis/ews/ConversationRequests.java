package com.vinchin.m365proxy.apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;

/**
 * Exchange Online用户组邮件管理相关接口
 */
public class ConversationRequests extends EwsBaseRequest {
    public ConversationRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }

}
