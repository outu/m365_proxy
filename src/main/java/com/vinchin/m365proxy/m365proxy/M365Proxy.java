/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365Proxy.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class M365Proxy {
    public static final Logger logger = LoggerFactory.getLogger(M365Proxy.class);
    public static void main(String[] args) throws Exception {
        boolean ret = true;
        M365ProxyApplication app = new M365ProxyApplication();

        // parser the arguments for m365_proxy, the port and uuid is necessary
        if (!app.parseArgs(args)){
            logger.error("parser arguments error");
            ret =  false;
        }

        if (app.getExit()){
            M365ProxyGlobalVals.g_service_exit_flag = true;
        }

        // global initialize
        if (ret && !M365ProxyGlobalVals.g_service_exit_flag){
            ret = app.globalInit();
            if (!ret){
                logger.error("global initialize error.");
            }
        }

        if(ret && !M365ProxyGlobalVals.g_service_exit_flag){
            logger.debug("m365_proxy start ...");
            ret = app.main(args);
            if (!ret){
                logger.error("m365_proxy started to listen and handle network events error.");
            }
        }

        app.globalCleanup();

        if (!ret){
            logger.error("m365_proxy exit failed !!!");
        } else {
            logger.debug("m365_proxy exit success !!!");
        }
    }
}
