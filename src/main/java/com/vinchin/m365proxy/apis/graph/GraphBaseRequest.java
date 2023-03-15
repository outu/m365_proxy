package com.vinchin.m365proxy.apis.graph;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.httpcore.HttpClients;
import com.microsoft.graph.requests.GraphServiceClient;
import com.vinchin.m365proxy.apis.BaseRequest;
import com.vinchin.m365proxy.apis.BaseUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class GraphBaseRequest extends BaseRequest {


    private int region = 0;
    private Map<String, String> authParameters;

    protected GraphServiceClient<Request> graphClient;

    protected String mail;

    public GraphBaseRequest(){

    }

    public GraphBaseRequest(Map<String, String> organizationRegionAuthParameters){
        authParameters = organizationRegionAuthParameters;
    }

    public void setGraphClient() throws Exception {
        initAuthParameters(BaseUtil.ApiTypeEnum.GRAPHAPI, authParameters);
        String token = getAccessToken();
        OkHttpClient httpClient = HttpClients.createDefault(getAuthenticationProvider(token));
        graphClient = GraphServiceClient.builder()
                .httpClient(httpClient)
                .buildClient();
    }


    public GraphServiceClient<Request> getGraphClient(){
        return graphClient;
    }


    private BaseAuthenticationProvider getAuthenticationProvider(String authToken) {
        String accessToken = authToken.replace("\"", "");

        return new BaseAuthenticationProvider() {
            @Override
            public CompletableFuture<String> getAuthorizationTokenAsync(final URL requestUrl) {
                if(this.shouldAuthenticateRequestWithUrl(requestUrl)) {
                    return CompletableFuture.completedFuture(accessToken);
                } else {
                    return CompletableFuture.completedFuture(null);
                }
            }
        };
    }
}
