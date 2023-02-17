package test;

import apis.ews.EwsBaseRequest;
import apis.ews.MessageRequests;
import apis.graph.GraphBaseRequest;
import apis.graph.common.UserRequests;
import apis.powershell.PowershellExchangeOperation;
import apis.soap.SoapBaseRequest;
import apis.soap.XmlRequestData;
import com.microsoft.graph.requests.GraphServiceClient;
import microsoft.exchange.webservices.data.core.ExchangeService;
import okhttp3.Request;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class testOuth {
    public List<Map>getExchangeServerSoapClient(String mailbox){
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@123456789");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        SoapBaseRequest soapBaseRequest = new SoapBaseRequest(organizationAuthParameters);
        soapBaseRequest.setSoapClient(mailbox);
        soapBaseRequest.setHttpContext();

        Map<String, HttpPost> soapClientMap = new HashMap<>();
        soapClientMap.put("soapClient", soapBaseRequest.getSoapClient());
        Map<String, HttpClientContext> httpContextMap = new HashMap<>();
        httpContextMap.put("httpContext", soapBaseRequest.getHttpContext());

        List<Map> soapClient = new ArrayList<>();
        soapClient.add(soapClientMap);
        soapClient.add(httpContextMap);

        return soapClient;
    }


    public ExchangeService getExchangeOnlineEwsClient(String mailbox) throws IOException {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", properties.getProperty("region"));

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(mailbox);

        return ewsBaseRequest.getEwsClient();
    }


    public ExchangeService getExchangeServerEwsClient(String mailbox){
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@123456789");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient(mailbox);

        return ewsBaseRequest.getEwsClient();
    }


    public GraphServiceClient<Request> getGraphClient() throws Exception {
        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();

        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", "0");
        organizationAuthParameters.put("username", properties.getProperty("yunqi@s22fb.onmicrosoft.com"));

        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();

        return graphBaseRequest.getGraphClient();
    }


    public PowershellExchangeOperation getPowershellClient(){
        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@123456789");
        organizationAuthParameters.put("protocol", "http");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");

        return new PowershellExchangeOperation(organizationAuthParameters);
    }




    private static int testSoapConnectExchangeServer() throws IOException {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@123456789");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        SoapBaseRequest soapBaseRequest = new SoapBaseRequest(organizationAuthParameters);
        soapBaseRequest.setSoapClient("Administrator@exch.com.cn");
        soapBaseRequest.setHttpContext();

        Map<String, HttpPost> soapClientMap = new HashMap<>();
        soapClientMap.put("soapClient", soapBaseRequest.getSoapClient());
        Map<String, HttpClientContext> httpContextMap = new HashMap<>();
        httpContextMap.put("httpContext", soapBaseRequest.getHttpContext());

        List<Map> soapClientCache = new ArrayList<>();
        soapClientCache.add(soapClientMap);
        soapClientCache.add(httpContextMap);

        apis.soap.MailRequests mailRequests = new apis.soap.MailRequests(soapClientCache);
        XmlRequestData xmlRequestData = new XmlRequestData();
        String xmlToGetMailMessage = xmlRequestData.buildXmlToGetMailMimeContent("test1@exch.com.cn", "AQMkAGJkZmFlNGJkLWM0NjEtNDU4Zi04NzhmLTNhNWE3OWYxMDFkOABGAAAD/21pFKhtgUqKvYTQLkeOAAcAqbn2gdfe6UikHCgMkkbpBQAAAgEPAAAAqbn2gdfe6UikHCgMkkbpBQAAAhjdAAAA");


        HttpResponse httpResponse = mailRequests.getResponseWithMimeContent(xmlToGetMailMessage);
        if(httpResponse.getStatusLine().getStatusCode() == 200){
            HttpEntity entity1 = httpResponse.getEntity();

            char[] readbuffer = new char[1024];

            BufferedReader xmlStreamReaderCache = new BufferedReader(new InputStreamReader(entity1.getContent()));
            int count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
            File txt = new File("F:\\soap_big2.xml");
            if(!txt.exists()){
                boolean result = txt.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(txt);
            while (count > 0) {
                String tmpString = String.copyValueOf(readbuffer);
                char[] tmpChar = new char[count];
                tmpString.getChars(0, count, tmpChar, 0);
                byte[] byteData = toBytes(tmpChar);
                fos.write(byteData, 0, byteData.length);
                fos.flush();
                count =  xmlStreamReaderCache.read(readbuffer, 0, 1024);
            }
            fos.close();
        } else {
            //System.out.println("error");
        }



        System.out.println(httpResponse.getStatusLine().getProtocolVersion() + " " + httpResponse.getStatusLine().getStatusCode());
        return ret;
    }


    private static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    private static int testPowershell() throws IOException {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@1234567890");
        organizationAuthParameters.put("protocol", "http");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        PowershellExchangeOperation powershellExchangeOperation = new PowershellExchangeOperation(organizationAuthParameters);
        String userInfo = powershellExchangeOperation.getUserInfo();
        System.out.printf(userInfo);

        return ret;
    }


    private static int testEwsConnectExchangeOnline() throws Exception {
        int ret = 0;

        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", properties.getProperty("region"));

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient("yunqi@s22fb.onmicrosoft.com");


        MessageRequests messageRequests = new MessageRequests(ewsBaseRequest.getEwsClient());
        //mailRequests.getMailRootFolder();
        //System.out.printf(mailRequests.getRootMailFolder());
       // mailRequests.getMimeContent("AAQkADE0ODViMDdkLWQ3MGItNDMyMi1hYzAyLWY0NDlhYTdjMjExMgMkABAAANMFII6LxUqBDnol2Q-6hBAAANMFII6LxUqBDnol2Q-6hA==");


        return ret;
    }


    private static int testEwsConnectExchangeServer() throws Exception {
        int ret = 0;

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();
        organizationAuthParameters.put("username", "Administrator@exch.com.cn");
        organizationAuthParameters.put("password", "backup@123456789");
        organizationAuthParameters.put("domain", "WIN-TT7P7PN7QHJ.exch.com.cn");
        organizationAuthParameters.put("region", "100");

        EwsBaseRequest ewsBaseRequest = new EwsBaseRequest(organizationAuthParameters);
        ewsBaseRequest.setEwsClient("Administrator@exch.com.cn");
        MessageRequests messageRequests = new MessageRequests(ewsBaseRequest.getEwsClient());
        messageRequests.syncGetMailFolder("AAMkAGE5NzcxZjBiLWI0Y2MtNDhlNy1hZjViLTQ0NzZiMmQzN2Q1ZAAuAAAAAACC2Y8PhSFoQo3NQPbM2L49AQBcaT0SLAv6S6PqbrxnTa5XAAAAAAEMAAA=", "");

        return ret;
    }


    private static int testGraphConnectExchangeOnline() throws Exception {
        int ret = 0;

        Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties"));

        Map<String, String> organizationAuthParameters = new HashMap<String, String>();

        organizationAuthParameters.put("tenantUuid", properties.getProperty("tenantUuid"));
        organizationAuthParameters.put("appUuid", properties.getProperty("appUuid"));
        organizationAuthParameters.put("appSecret", properties.getProperty("appSecret"));
        organizationAuthParameters.put("appCertInfo", properties.getProperty("appCertInfo"));
        organizationAuthParameters.put("region", "0");
        organizationAuthParameters.put("username", properties.getProperty("yunqi@s22fb.onmicrosoft.com"));

        GraphBaseRequest graphBaseRequest = new GraphBaseRequest(organizationAuthParameters);
        graphBaseRequest.setGraphClient();
        UserRequests userRequests = new UserRequests(graphBaseRequest.getGraphClient());
        //System.out.printf(userRequests.syncUserInfoByDeltaLink("https://graph.microsoft.com/v1.0/users/microsoft.graph.delta?$deltatoken=7U_SxArXp1Fr05OO5oNYmJqxFcLUyWxAbyI-WHAsGfDwqRvNlIQ73c1_VHC2noiocoNSn5UR0GMtWspMwJw39oAtYKeHXkVfN3rYLIoPYN2vss1tc3heHZy14PhOLaLHFs7qmm4w8Xns7t3LZ4cfH1pKCiWEQAwe4ZH528_FQYgz5bLc56coRKxQqciSzKRKmGU54_zpLi8x12vik85O5r493ditz6t-_UPi4gJfk6eyMKFGdOdTHlLhwdtTM6315Eyzc9_0oygp6amcn6oGbbCqMbWXqFeRbdh6b9kXU9_cCpTmh0ILQCT1MGFxS9XbQABXYwyCBI17sLTbeS5Ek-FXzEhMrzvMAUTOsRmNubRzg_LfEtcIgKDUx0KUy1hKACcBMRw9AlLlqRqF31wRjyk5gmqN3FLRZOh76OtiNgDHv44WU7-ZopIiQdRxGkS5hhJu4mXkvDMmv57BTTTH6KNdwQAyJ80XKoEvfgUPpwqh1yhittzxEC_-HyB3dUllX8VYoKG-A1ULJTpYvDL8Jx6npGjaxABB0Z_VCzwybYf0jEvVfAnxSVw6JiVj-8MhRGfz5gRqN_FSsytOe8drDiATlW-T8NcoOmFuxxlmThgkt3S64dbg0ONyUJTA2XsX1fxB_q1wjB6Zi4e-0PDQtebnx8sp65z3TirmVIHYf-UashAIiMdQjkXqeq3L8Z65_1PRFzEv3UrRZwmHwMX9fWbLN1TrBOj9o5dcpmzCYaG_37XvfzZh8MM1lwT05172x4GKZtKSKi8jF96XXZoQlpBmiXqJmWn6aiUr2XoyR7bimGn_kFb6o7WaIr_sM-rLZxFgUDSu6hxet5XMfJKU-SOCfcpShWSYoXbsD-7-e2n39uiPUONeKrC5wJO-8mnqhVNt7Ve4uPCQPcVOEFqqSjWpW-vptUAOPhw0Rbq0JT8u9edZtLMR2Oo159wMIEqz9rHknylR7nISS67PHRp1vE71L5M7dD8vuSaSzJkEekicVW8Q5-k_D9neD-idL1J73Ykz24AwS8ZyA3C2346P_Fr31RPOPufAa4Dx6e2Ipbl24Gke_v8XlzTXnFF2WHfLGMJVVtyrwLx_KAqxIJUqAVi2iVWAyRjl544YCmCyQI_dIMBKTVQZyzlRA7zAFQBPIB0659w0u5t-By0QTJA1t7oBBfZmx6BUgSthwAnCYJ1FrWHFinesOdyngXDN1h4zXBpj90LikxKFXCXpmcwerhN-FxU9tAjNBf0nVDBcvhv8Cwj3r8UoRLNmfK2Oc1q2f1lxXJzNurbnjbVa48nXlxDfqohABduePmXQXpnZqoT40B_GWXjwTnH03A24BBIL04WPC78YKnyOoQDeU_H2snJkdeu32DgH0SmjHTyXF8A4h3YhJMrLS-a0rfYNoSbTQhYpfpXKImb--7oY1wJOjFbySmCNyorj9rC0Wrt9DbTYAjXYeuWCtt2VjHwfZaPF4q30LYVmOEg9IWmqEEqCmbp7LnmDIntj9Ii4z_41bzCYNcbIy_wBfiHgrzuRoMe6m23xtLn_0oEJyf3lhf6C0kWyPUHygfZQwgr4xxPGSpLpKR1s1F1Q4v03JROW8FH0zcXOiOSKOIi4jqIcZUciRKfHjU-_RglGuHtL5fQ7ZozamaSM3n8cFHFjmpxDpdztBoAbiUqo8LSfJU3-R50yXrL3ulHWXMdl08ONcTOGjQCLz3sTwUazgIzqJmfJELZivrRncjB2fB9fyDi75XCHfCTXgdX2nasNy4IEu-uUOhkKAATW0gwEfHyedEzdwVPS5pDxFnpWNYFt-Iz7foiCATB57aIJc0Ek782birdFdeDSiniSO90kZiyOTjax2Ajk_79Y8Nm7cOAWr6Wtp_HjusMa4PdlBUSQwhNGyx3xDlBV7j0ltACu2XBTASpR7moHgeDCvPKG6EZbXAaAh1dwJnj7DHNJUKaAJHgLgDNT_SowRDQGZ8XO0qiSaVTx7Yd_MN3noES-k-FeAKXB_aowRiJyyKo9fvn5dtw4neJGhHQjBjPv-ynO7hsU4fOYkSZKQSC5kBd6dsKA_3UGvGvXpwqjJohWybRKWBoK23iNOIO9qzZUaFRhlSE2wH6PwozHBaGhPRlQfefPsPMUdM8yG_yVKXbQrbuoXLvmh40A6Jpxh-OgIRIy4uM5-_AFUgA0KRj-8GUBP76On5q7nMak3goU8CVuG9JQHVTFweJWqO2tdny-kPfYon3u-Ks6aXQ7R5-rJ6I6MyRCYFGawUw95cVVvVY9IW7OjOOuCxfTthOxyo6PrE1z2R8qi6iyyvDH5zRd8HxSWS0iKhELO7w71Ykbry3JRH1_wT9ly177ngh34uNWyfPD-Wmi6u6c7W3cVCphjYYHViZQQDM80CraeaTP6Cb5Po0tsj6QAXkBdphG3FZLKS_uwBwpxWJOKsebTWQa37bZxJk4DOR1IFqksIVYbkw7QXr42VGEdwKjlXJCSEHy5R5Eq7nXCDfG_jJDNorQwNta8fgFHJD31r41Ybbxetu7esNW-AmKQCKQYPLzHvf77tfp9caLc4OamCCQRfZ1YTbPuPf4eqMyj5_loNEJ2qgkrGTpnPENAXWpV4hoacI_gof9Lmb-9tJAGhz3FSOd4F1N4IQUUzMdzQNymiVu30s8c5toY7psxChSb8oT2Ta-e_DShSRT6mITFkRUbHQugfdxzC1-L_44PLSVmB7rEcENLTj8M3PP2gaKo4WF0kV-XILAuwvXrMWud8bhpHQAy4oTWEaiGKMlfad-xu0iE2kPfkYxSnvi_rGywJ9r44NM4CxywA6HueQphx4D2gbULNDtXJq9JAqueeG4dNNHVFnxz9d7TblBHycha1Wum-UF3HHdSMUP4_mrms4JBxbkKMSJxyDnh-2rJI69othrd2bRrgs5qbQqJaMeUrzAmp6d57vYA4FwItgg3AZvFhUgYVrk_1qW14okHJUeARHWNfqt2EzyFsdi2ihaTM3M_dA-W3cJcfONlKSVMpcu8eopyGm8Tb4H4o8dgSVRsfYIEXOaJ4rDD9zYYu_-WpMrh_CWsP-8Nrg9sARvf7aF5SDUDImqpLaVqLiafa5StN-N-u9sQoF398txqFDtcwh0PYVHw1GCjVeSvU9R1Bt2B9zkj_y27XX2zdygnybweW8wt8PfPV21P1e4ZFzjQvUS57DuHrEVuAzzmkTnUkme8Be7YzKvoDvNXTk_hhFE0YbYpmV257XJZXywIo-SbEB5c1aGITh-3Y1EMyuIoCcRGyLupWkoxTIHdLum844tSismJyiQuFFJOKWJBI98RPEpUEYEgv-klDz39iR59YCgT8rIwHuLOF4uwPXxERua2LikwItFoNV3eUojALqdB4CpT1nidLKYpxapXfAHhrqkDVoi_xlfXFcDEVvkdPrLMh_K1AqRA8zeBhs6KLzJqww5NRmIXgT0cif4W6A1vzCuL4_YzRkGDOyzXlvpghVAbPrONvomF_3XkWjK5LqpCvmJGfOz1TRng17FEDeRXf1sTA7_Gm7ZgnA56nnneUi4pYdFpNz1WPl1tHyx46f2sjBQ7AZuP8VEB9BDhsY1o0C4mQLPKIsVt1oYkLVKCZS90Plo7-LU0N921Xh50ivI0Kb3Gmudii2T424Sryy71HJk0FLIGusbfD66H0hJUsurMRqEyECaMgB0w7bRXoAeRhQToj3CBllsrsksb54BQFNnVrZY5fMx40nQWPMIz4JUNBcQFnKQwYDwgRsSH19ceimhkxyU-hligLppaxSHBsz3pYEkPlT3c1A852Oai4WLzaC--XKM1YO92mYFAak8wCNlB9fg6d67pF-Iupi13biRoh5Llsk4t7UItR2fUFX_wJB3zqR7m-mKzMb6pYplUnVpET4DOg5bDiKuG0B_iCPopubaOpJqtmFuPRw3TsdTfS9LYgkCIvfEM5llO6hxE4bXMRuQroc3u-G0B9Gc1X4cO0UviBTVVB_fChdWshvA10RZePcLzm8wHf3F6hVcexWggRb6h57acD0gUx7Iqp6IB7RfArKRIb0WOe_CWO1By62UI47aQ9g3dIRv5DrDzx-uajkt8-ArRD3glh11KlknrgrXga6KGWIrDfvux279RoDTtwC0ZG_WNRT3xojfrg7mQjN4FxnZOCZpSb-qVely4y6WXjZClQ72omLoOvykraZvG77pIm0935_wkbWlZ-O-g1N6wDA9FYWXFgNX2g0t_un2Tu72UszINeuy8dBgVuqvD0pjqOEtnFlQS4QBmi1MbawaBFK-gtCg-0IoNEI1VpjHlh5eI8OoYikK8i8PB2cG8LzUfS_uXKHfHGUB9tc8D1K6SX3qGrcy91bpPiP1ToZ1BAzWgLTUibmgotpzUw.6UQqc_53v78XkXJnhIJXQ1OEB-x8FQNpuV0XruUQblM", ""));

        return ret;
    }
}
