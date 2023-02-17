package apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.ItemId;

public class TaskRequests extends EwsBaseRequest{
    public TaskRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }
    public byte[] getStructContent(String taskId) throws Exception {
        taskId = taskId.replace("-", "/");
        taskId = taskId.replace("_", "+");

        try {
            ItemId itemId = new ItemId(taskId);
            PropertySet propSet = new PropertySet(BasePropertySet.FirstClassProperties);
            propSet.add(ItemSchema.MimeContent);

            EmailMessage message = EmailMessage.bind(ewsClient, itemId, propSet);

            return message.getMimeContent().getContent();
        } catch (Exception e){
            throw new Exception(e);
        }
    }


    public String getTaskFolder(String rootFolder)
    {
        return "";
    }


    public String getTaskIndexInfo(String folderId, int count)
    {

        return "";
    }
}
