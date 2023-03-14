package com.vinchin.m365proxy.apis.soap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

public class MailRequests extends SoapBaseRequest {
    //private String requestMailMimeContentXml = "";

    public MailRequests(HttpPost httpPost, HttpClientContext httpClientContext){
        soapHttpClient = httpPost;
        httpContext = httpClientContext;
    }


    public BufferedReader getResponseWithMimeContent(String xml){
        boolean ret = doStreamSoapRequest(xml);

        return getXmlStreamReaderCache();
    }
}
