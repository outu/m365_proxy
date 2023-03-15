/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	M365ProxyApplication.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package com.vinchin.m365proxy.m365proxy;

import static com.vinchin.m365proxy.m365proxy.M365ProxyError.BdErrorCode.BD_GENERIC_SUCCESS;

public class M365ProxyApplication {

    String appName = "M365_proxy";

    int threadPoolNum = 30;

    public boolean exitFlag = false;

    public int listenPort;

    public String processUuid;

    public boolean main(String[] args) {
        M365ProxyListenConnection m365_proxy_listen_connection = new M365ProxyListenConnection();
        m365_proxy_listen_connection.init(listenPort, processUuid, threadPoolNum);
        int ret = m365_proxy_listen_connection.run();

        if (BD_GENERIC_SUCCESS.getCode() != ret){
            return false;
        } else {
            return true;
        }
    }

    /**
     * @Description : parse args
     * @param args arg value
     * @return true-success, false-error
     */
    public boolean parseArgs(String[] args){

        if (0 == args.length){
            return false;
        }

        int argCount = 0;

        for(String arg : args) {
            argCount++;
            String tmpArg = arg.substring(0, 2);
            switch (tmpArg) {
                case "-h":
                    showHelp();
                    exitFlag = true;
                    return true;
                case "-v":
                    showVersion();
                    exitFlag = true;
                    return true;
                case "-p":
                    if (arg.contains("-p")){
                        listenPort = Integer.parseInt((arg.substring(2, arg.length())));
                    }
                    break;
                case "-u":
                    if (arg.contains("-u")){
                        processUuid = (arg.substring(2, arg.length()));
                    }
                    break;
                default:
                    if (argCount <= 2){
                        return false;
                    }
            }
        }

        return true;
    }


    /**
     * @Description : get application exit flag
     * @return true-exit，false-no exit
     */
    public boolean getExit(){
        return exitFlag;
    }

    /**
     * @Description : global init
     * @return true
     */
    public boolean globalInit() {
        //init logpack
        M365ProxyConfig.initLogback();
        //init global exit flag
        M365ProxyGlobalVals.gServiceExitFlag = false;

        return true;
    }

    /**
     * @Description global clean up
     * @return true
     */
    public boolean globalCleanup() {

        return true;
    }

    /**
     * @Description : show help
     */
    public void showHelp(){
        String helpString = "";

        helpString += appName + ": Vinchin backup and recovery system\n";
        helpString += "\nUsage: " + appName + "[options]\n";
        helpString += "Options:\n"
            +"  -h, --help			            Display this help message and exit.\n"
            +"  -v, --version			        Display version information and exit.\n"
            +"  -p, --port [port number]	    The listen port of this program, it is unique.\n"
            +"  -u, --uuid	                    Progress unique identification .\n";

        System.out.println(helpString);
    }

    /**
     * @Description : show version
     */
    public void showVersion(){
        String version = "";

        version += appName + " v2.0\n";
        version += "Copyright (c) 2014-2023 Vinchin, Inc.";

        System.out.println(version);
    }
}
