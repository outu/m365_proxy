package com.vinchin.m365proxy.apis.ews;

import com.alibaba.fastjson.JSONObject;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.search.FindFoldersResults;
import microsoft.exchange.webservices.data.search.FolderView;

import java.util.ArrayList;
import java.util.List;

public class FolderRequests extends EwsBaseRequest {
    public FolderRequests(ExchangeService ewsClientCache){
        ewsClient = ewsClientCache;
    }

    public String getFolderInfo(WellKnownFolderName folderName, int type, int folderNum) throws Exception {
        Folder folder = Folder.bind(ewsClient, folderName);
        JSONObject folderInfo = new JSONObject();
        folderInfo.put("folder_id", folder.getId().getUniqueId());
        folderInfo.put("parent_folder_id", folder.getParentFolderId().getUniqueId());
        folderInfo.put("display_name", folder.getDisplayName());
        folderInfo.put("type", type);
        folderInfo.put("folder_num", folderNum);

        return folderInfo.toJSONString();
    }



    /**
     * 获取Exchange Server and Exchange Online各种类型顶级目录
     * @return
     * @throws Exception
     */
    public String getAllTypeRootFolder() throws Exception {
        String rootMailFolderJson = "";
        List<JSONObject> rootMailFolderListObject = new ArrayList<>();

        Folder folder = Folder.bind(ewsClient, WellKnownFolderName.MsgFolderRoot);
        FindFoldersResults findFolderResults = folder.findFolders(new FolderView(EwsUtil.MAX_ROOT_FOLDER_COUNT));

        for (Folder item : findFolderResults.getFolders()) {
            int type = getRootFolderType(item.getFolderClass());
            if (type == -1) {
                continue;
            }
            JSONObject oneRootMailFolder = new JSONObject();
            oneRootMailFolder.put("folder_id", item.getId().getUniqueId());
            oneRootMailFolder.put("parent_folder_id", item.getParentFolderId().getUniqueId());
            oneRootMailFolder.put("display_name", item.getDisplayName());
            oneRootMailFolder.put("type", type);
            rootMailFolderListObject.add(oneRootMailFolder);
        }

        rootMailFolderJson = rootMailFolderListObject.toString();

        return rootMailFolderJson;
    }



        public int getRootFolderType(String rootFolderType){
        int type;

        if (rootFolderType == null){
            return -1;
        }

        switch (rootFolderType){
            case "IPF.Note":
                type = 0;
                break;
            case "IPF.Appointment":
                type = 1;
                break;
            case "IPF.Contact":
                type = 2;
                break;
            case "IPF.Task":
                type = 3;
                break;
            default:
                type = -1;
        }

        return type;
    }
}
