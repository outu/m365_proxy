package apis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class BaseRequest {
    /**
     * Microsoft365 Authentication connection requires parameters
     */

    private String scope;
    private String appUuid;

    private String appSecret;
    //证书认证
    private String appCertInfo;

    private String instance;
    private ConfidentialClientApplication app;


    protected void initAuthParameters(BaseUtil.ApiTypeEnum apiType, Map<String, String> authParameters){
        appUuid     = authParameters.get("appUuid");
        appSecret   = authParameters.get("appSecret");
        appCertInfo = authParameters.get("appCertInfo");

        String region  = authParameters.get("region");


        String tenantUuid = authParameters.get("tenantUuid");

        BaseUtil.RegionEnum regionEnum = BaseUtil.RegionEnum.getRegionEnumByRegion(Integer.valueOf(region));

        if(regionEnum != null){
            switch (regionEnum){
                case GLOBALCLOUD:
                    instance = "https://login.microsoftonline.com/" + tenantUuid + "/";
                    if (apiType == BaseUtil.ApiTypeEnum.EWSAPI){
                        scope = "https://outlook.office365.com/.default";
                    } else {
                        scope = "https://graph.microsoft.com/.default";
                    }
                    break;
                case CHINACLOUD:
                    instance = "https://login.chinacloudapi.cn/" + tenantUuid + "/";
                    scope = "https://partner.outlook.cn/.default";
                    break;
                default:
                    instance = "https://login.microsoftonline.com/" + tenantUuid + "/";
                    scope = "https://outlook.office365.com/.default";
                    break;
            }
        }
    }


    private void BuildConfidentialClientObject() throws Exception {
        if(appCertInfo == null){
            app = ConfidentialClientApplication.builder(
                            appUuid,
                            ClientCredentialFactory.createFromSecret(appSecret))
                    .authority(instance)
                    .build();
        } else {
            JsonObject appCertInfoJsonObject = JsonParser.parseString(appCertInfo).getAsJsonObject();
            String certContent = appCertInfoJsonObject.get("cert_content").getAsString();
            String certPassword = appCertInfoJsonObject.get("cert_password").getAsString();

            InputStream pkcs12Certificate = new ByteArrayInputStream(certContent.getBytes());
            app = ConfidentialClientApplication.builder(
                            appUuid,
                            ClientCredentialFactory.createFromCertificate(pkcs12Certificate, certPassword))
                    .authority(instance)
                    .build();
        }
    }


    private IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {

        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                        Collections.singleton(scope))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }


    public String getAccessToken() throws Exception {
        BuildConfidentialClientObject();
        IAuthenticationResult result = getAccessTokenByClientCredentialGrant();
        return result.accessToken();
    }
}
