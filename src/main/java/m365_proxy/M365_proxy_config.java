/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365_proxy_config.java: M365_proxy config file
 * Author		:	yangjunjie
 * Date			:	2023/02/22
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.io.File;

public class M365_proxy_config {
    /**
     *
     */
    public static void initLogback() {
        String logPackXml = "";
        String osName = System.getProperty("os.name");

        if (osName != null && osName.startsWith("Windows")){
            logPackXml = "./config/logpack.win32.xml";
        } else {
            logPackXml = "/etc/vinchin/m365_proxy/logpack.linux.xml";
        }

        File logPackFile = new File(logPackXml);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(loggerContext);
        loggerContext.reset();
        try {
            joranConfigurator.doConfigure(logPackFile);
        } catch (Exception e) {
            System.out.println(String.format("Load logback config file error. Message: ", e.getMessage()));
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
    }
}
