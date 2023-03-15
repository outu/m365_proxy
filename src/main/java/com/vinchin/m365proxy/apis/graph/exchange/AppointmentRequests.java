package com.vinchin.m365proxy.apis.graph.exchange;

import com.microsoft.graph.requests.GraphServiceClient;
import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import okhttp3.Request;

import java.util.List;
import java.util.Map;

public class AppointmentRequests extends GraphBaseRequest {
    public AppointmentRequests(GraphServiceClient<Request> graphClientCache, String mailCache){
        graphClient = graphClientCache;
        mail = mailCache;
    }

}
