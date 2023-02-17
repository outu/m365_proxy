package test;

import apis.ews.FolderRequests;
import apis.ews.MessageRequests;
import apis.powershell.PowershellExchangeOperation;
import microsoft.exchange.webservices.data.core.ExchangeService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class testExchangeServerApis {
    private static List<Map> soapClient;
    private static ExchangeService ewsClient;
    private static PowershellExchangeOperation powershellClient;

    private static byte[] mimecontentCache;

    public static void main(String[] args) throws Exception {
        String mailbox = "Administrator@exch.com.cn";
        initClient(mailbox);
        int size,nextSize= 0;
        int num = getMimeContentInfo("AAMkAGE5NzcxZjBiLWI0Y2MtNDhlNy1hZjViLTQ0NzZiMmQzN2Q1ZABGAAAAAACC2Y8PhSFoQo3NQPbM2L49BwBcaT0SLAv6S6PqbrxnTa5XAAAAAAEMAABcaT0SLAv6S6PqbrxnTa5XAABu5XMDAAA=");
        System.out.printf(String.valueOf(num));
        if (num > 1024){
            size = 1024;
            nextSize = num - 1024;
        } else {
            size = num;
        }
        byte[] read = new byte[size];
        ByteArrayInputStream in = new ByteArrayInputStream(mimecontentCache);
        in.read(read);
        System.out.printf(new String(read));
        byte[] read1 = new byte[nextSize];
        in.read(read1);
        System.out.printf(new String(read1));
    }


    private void test() throws IOException {
        int size,nextSize= 0;
        int num = 1024;

        if (num > 1024){
            size = 1024;
            nextSize = num - 1024;
        } else {
            size = num;
        }
        byte[] read = new byte[size];
        ByteArrayInputStream in = new ByteArrayInputStream(mimecontentCache);
        in.read(read);
        System.out.printf(new String(read));
        byte[] read1 = new byte[nextSize];
        in.read(read1);
        System.out.printf(new String(read1));
    }


    public static void initClient(String mailbox){
        testOuth testOuth = new testOuth();
        soapClient = testOuth.getExchangeServerSoapClient(mailbox);
        ewsClient  = testOuth.getExchangeServerEwsClient(mailbox);
        powershellClient = testOuth.getPowershellClient();
    }


    /**
     * 获取所有的用户信息
     * @return
     * @throws IOException
     */
    public static String getUserInfo() throws IOException {
        return powershellClient.getUserInfo();
    }


    /**
     * 获取所有的顶级目录
     * @return
     * @throws Exception
     */
    public static String getAllTypeRootFolder() throws Exception {
        FolderRequests folderRequests = new FolderRequests(ewsClient);
        return folderRequests.getAllTypeRootFolder();
    }


    /**
     * 增量获取目录
     * @return
     * @throws Exception
     */
    public static String syncGetMailFolder(String rootFolderId, String folderDeltaToken) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        return messageRequests.syncGetMailFolder(rootFolderId, folderDeltaToken);
    }


    /**
     * 增量获取邮件
     * @param folderId
     * @param count
     * @param messageDeltaToken
     * @return
     * @throws Exception
     */
    public static String syncGetMessageInfo(String folderId, int count, String messageDeltaToken) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        return messageRequests.syncGetMessageInfo(folderId, count, messageDeltaToken);
    }


    /**
     * 获取Exchange Online邮件详细信息，Graph API获取不全
     * @param messageId
     * @return
     * @throws Exception
     */
    public static String getMessageDetailInfo(String messageId) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        return messageRequests.getMessageDetailInfo(messageId);
    }


    public static int getMimeContentInfo(String messageId) throws Exception {
        MessageRequests messageRequests = new MessageRequests(ewsClient);
        mimecontentCache = messageRequests.getMimeContent(messageId);

        return mimecontentCache.length;
    }
}
