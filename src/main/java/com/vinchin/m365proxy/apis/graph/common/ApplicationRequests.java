/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	ApplicationRequests.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/03/06
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.apis.graph.common;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.graph.models.Application;
import com.microsoft.graph.models.RequiredResourceAccess;
import com.microsoft.graph.models.ResourceAccess;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.ApplicationCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.vinchin.m365proxy.apis.graph.GraphBaseRequest;
import okhttp3.Request;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRequests extends GraphBaseRequest {
    public ApplicationRequests(GraphServiceClient<Request> graphClientCache){
        graphClient = graphClientCache;
    }

    public List<String> getAzureADAppAuthResourceAccess(String appId){
       List<String> appAccessResource = new ArrayList<>();

        try {
            String filter = String.format("appId eq '%s'", appId);
            ApplicationCollectionPage applications = graphClient.applications()
                    .buildRequest()
                    .filter(filter)
                    .get();
            Application application = applications.getCurrentPage().get(0);

            int size = application.requiredResourceAccess.size();
            for (int i=0; i<size; i++){
                RequiredResourceAccess resourceAccess = application.requiredResourceAccess.get(i);
                for (ResourceAccess access : resourceAccess.resourceAccess) {
                    appAccessResource.add(access.id.toString());
                }
            }
            return appAccessResource;
        } catch (Exception e) {
            System.out.println(e);
            return appAccessResource;
        }
    }
}
