/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyGlobalVals.java: Set global variables that apply the entire lifecycle
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy;

import com.microsoft.graph.requests.GraphServiceClient;
import com.vinchin.m365proxy.apis.powershell.PowershellExchangeOperation;
import com.vinchin.m365proxy.apis.soap.SoapBaseRequest;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class M365ProxyGlobalVals {
    public static boolean gServiceExitFlag = false;
    public static Map<String, ExchConnCache> gExchConnCaches = new HashMap<>();

    public static class ExchConnCache{
        public ExchangeService ewsClient = null;
        public GraphServiceClient<Request> graphClient = null;
        public SoapBaseRequest soapClient = null;
        public Map<String, String> organizationAuthParameters;
        public String mail = "";
    }
}
