package apis.soap;

import apis.BaseRequest;
import apis.BaseUtil;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static apis.BaseUtil.RegionEnum.LOCAL;

public class SoapBaseRequest extends BaseRequest {
    private String tokenEndPoint = null;
    private int region = 0;
    private Map<String, String> authParameters;

    protected HttpPost soapClient = null;

    protected HttpClientContext httpContext = null;

    public SoapBaseRequest(){

    }

    public SoapBaseRequest(Map<String, String> organizationRegionAuthParameters){
        region = Integer.parseInt(organizationRegionAuthParameters.get("region"));

        switch (BaseUtil.RegionEnum.getRegionEnumByRegion(region)){
            case  GLOBALCLOUD:
                tokenEndPoint = "https://outlook.office365.com/EWS/Exchange.asmx";
                break;
            case CHINACLOUD:
                tokenEndPoint = "https://partnet.outlook.cn/EWS/Exchange.asmx";
                break;
            case LOCAL:
                String domain = organizationRegionAuthParameters.get("domain");
                tokenEndPoint = "https://" + domain + "/EWS/Exchange.asmx";
                break;
            default:
                break;
        }

        authParameters = organizationRegionAuthParameters;
    }


    public void setSoapClient(String mailbox){
        try {
            soapClient = new HttpPost(tokenEndPoint);
            soapClient.addHeader("Content-type", "text/xml; charset=utf-8");
            soapClient.addHeader("User-Agent", "EWS");
            soapClient.addHeader("Accept", "text/xml");
            soapClient.addHeader("Keep-Alive", "300");
            soapClient.addHeader("Connection", "Keep-Alive");
            soapClient.addHeader("Accept-Encoding", "gzip,deflate");
            soapClient.addHeader("X-AnchorMailbox", mailbox);

            if (BaseUtil.RegionEnum.getRegionEnumByRegion(region) == LOCAL){
                RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                        .setAuthenticationEnabled(true)
                        .setRedirectsEnabled(true)
                        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM));

                soapClient.setConfig(requestConfigBuilder.build());
            } else {
                initAuthParameters(BaseUtil.ApiTypeEnum.EWSAPI, authParameters);
                String token = getAccessToken();
                soapClient.addHeader("Authorization", "Bearer " + token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setHttpContext(){
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        if (BaseUtil.RegionEnum.getRegionEnumByRegion(region) == LOCAL){
            NTCredentials webServiceCredentials = new NTCredentials(authParameters.get("username"), authParameters.get("password"), "", "");
            AuthScope authScope = new AuthScope(AuthScope.ANY);
            credentialsProvider.setCredentials(authScope, webServiceCredentials);
            httpContext = HttpClientContext.create();
            httpContext.setCredentialsProvider(credentialsProvider);
        } else {
            httpContext = HttpClientContext.create();
        }
    }


    public SSLConnectionSocketFactory getSSLConnectionSocketFactory() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        TrustStrategy   acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContexts = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();

        return new SSLConnectionSocketFactory(sslContexts, new NoopHostnameVerifier());
    }


    public HttpPost getSoapClient(){
        return soapClient;
    }


    public HttpClientContext getHttpContext(){
        return httpContext;
    }
}
