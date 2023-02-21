/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import common.TypeConversion;

public class M365_proxy {
    public static void main(String[] args) throws Exception {
        boolean ret = true;
        M365_proxy_application app = new M365_proxy_application();

        // parser the arguments for m365_proxy, the port and uuid is necessary
        if (!app.parseArgs(args)){
            System.out.println("parser arguments error");
            ret =  false;
        }

        if (app.getExit()){
            M365_proxy_global_vals.g_service_exit_flag = true;
        }

        // global initialize
        if (ret && !M365_proxy_global_vals.g_service_exit_flag){
            ret = app.globalInit();
            if (!ret){
                System.out.println("global initialize error.");
            }
        }

        if(ret && !M365_proxy_global_vals.g_service_exit_flag){
            ret = app.main(args);
            if (!ret){
                System.out.println("m365_proxy started to listen and handle network events error.");
            }
        }

        app.globalCleanup();

        if (!ret){
            System.out.println("m365_proxy exit failed !!!");
        } else {
            System.out.println("m365_proxy exit success !!!");
        }
    }
}
