package apis.soap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.List;
import java.util.Map;

public class MailRequests extends SoapBaseRequest{
    //private String requestMailMimeContentXml = "";

    public MailRequests(List<Map> soapClientList){
        soapClient = (HttpPost) soapClientList.get(0).get("soapClient");
        httpContext = (HttpClientContext) soapClientList.get(1).get("httpContext");
    }


    public HttpResponse getResponseWithMimeContent(String xml){
        try {
            SSLConnectionSocketFactory connectionSocketFactory = getSSLConnectionSocketFactory();
            StringEntity entity = new StringEntity(xml);
            soapClient.setEntity(entity);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();

            return httpClient.execute(soapClient, httpContext);
        } catch (Exception e){
            return null;
        }
    }
}
