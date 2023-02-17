package apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.service.item.Conversation;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.property.complex.ItemId;
import microsoft.exchange.webservices.data.search.ConversationIndexedItemView;

import java.util.List;

/**
 * Exchange Online用户组邮件管理相关接口
 */
public class ConversationRequests extends EwsBaseRequest{
    public ConversationRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }

}
