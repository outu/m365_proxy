/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_proxy_global_vals.java: Set global variables that apply the entire lifecycle
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import com.microsoft.graph.requests.GraphServiceClient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class M365_proxy_global_vals {
    public static boolean g_service_exit_flag = false;
    public static Map<String, ExchConnCache> g_exch_conn_caches = new HashMap<>();

    public static class ExchConnCache{
        public ExchangeService _ewsClient = null;
        public GraphServiceClient<Request> _graphClient = null;
        public Map<String, String> _organizationAuthParameters;
        public String _mail = "";
    }
}
