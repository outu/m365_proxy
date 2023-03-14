package com.vinchin.m365proxy.apis.soap;

import com.vinchin.m365proxy.apis.BaseRequest;
import com.vinchin.m365proxy.apis.BaseUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;


import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import static com.vinchin.m365proxy.apis.BaseUtil.RegionEnum.LOCAL;

public class SoapBaseRequest extends BaseRequest {
    private String tokenEndPoint = null;
    private int region = 0;
    private Map<String, String> authParameters;
    private CloseableHttpClient httpClient;
    private BufferedReader xmlStreamReaderCache;
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

    public String doSoapRequest(String xml){
        StringBuilder responseXmlData = new StringBuilder();

        try {
            SSLConnectionSocketFactory connectionSocketFactory = getSSLConnectionSocketFactory();
            StringEntity entity = new StringEntity(xml);
            soapClient.setEntity(entity);
            httpClient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();
            HttpResponse httpResponse = httpClient.execute(soapClient, httpContext);
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                HttpEntity entity1 = httpResponse.getEntity();

                char[] readbuffer = new char[1024];

                xmlStreamReaderCache = new BufferedReader(new InputStreamReader(entity1.getContent()));
                int count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
                while (count > 0) {
                    responseXmlData.append(String.copyValueOf(readbuffer, 0, count));
                    count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
                }

                xmlStreamReaderCache.close();
                httpClient.close();

                return responseXmlData.toString();
            } else {
                return null;
            }
        } catch (Exception e){
            return null;
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

    public void forceDestroyHttpResource(){
        try{
            xmlStreamReaderCache.close();
            httpClient.close();
        } catch (Exception e){
            //do nothing
        }
    }
}
