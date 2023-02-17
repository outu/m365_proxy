package apis.ews;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.BasePropertySet;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.enumeration.service.SyncFolderItemsScope;
import microsoft.exchange.webservices.data.core.request.FindItemRequest;
import microsoft.exchange.webservices.data.core.request.SyncFolderItemsRequest;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Contact;
import microsoft.exchange.webservices.data.core.service.item.ContactGroup;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.EmailAddressDictionary;
import microsoft.exchange.webservices.data.property.complex.FolderId;
import microsoft.exchange.webservices.data.property.complex.GroupMember;
import microsoft.exchange.webservices.data.property.complex.GroupMemberCollection;
import microsoft.exchange.webservices.data.sync.ChangeCollection;
import microsoft.exchange.webservices.data.sync.ItemChange;

import java.util.List;

public class ContactRequests extends EwsBaseRequest {
    public ContactRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }
    public void getContactGroup() throws Exception {
        Folder folder = Folder.bind(ewsClient, WellKnownFolderName.Contacts);
        FolderId folderId = new FolderId(folder.getId().getUniqueId());
        SyncFolderItemsRequest syncFolderItemsRequest = new SyncFolderItemsRequest(ewsClient);
        ChangeCollection<ItemChange> itemChangeCollection = syncFolderItemsRequest.getService().syncFolderItems(folderId, PropertySet.FirstClassProperties, null, 10, SyncFolderItemsScope.NormalItems, "");
        int changeCount = itemChangeCollection.getCount();
        for (int i=0; i<changeCount; i++){
            ItemChange itemChange = itemChangeCollection.getChangeAtIndex(i);
            Item item = itemChange.getItem();
            if (item instanceof Contact){
                Contact contact = (Contact) item;
               // System.out.printf(contact.getDisplayName());
            } else {
                PropertySet propSet = new PropertySet(BasePropertySet.FirstClassProperties);
                ContactGroup contactGroup = ContactGroup.bind(ewsClient, item.getId(), propSet);


                //System.out.printf(contactGroup.getMimeContent().getContent().toString());
                GroupMemberCollection groupMemberCollection = contactGroup.getMembers();
                int memberCount = groupMemberCollection.getCount();
                List<GroupMember> list = groupMemberCollection.getItems();
                for (int j=0; j<memberCount; j++){
                    GroupMember groupMember = list.get(j);
                    System.out.printf(groupMember.getAddressInformation().getAddress());
                }
            }
        }

    }
}
