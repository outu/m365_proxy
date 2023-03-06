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

    String _app_name = "M365_proxy";

    int _thread_pool_num = 30;

    public boolean _exit_flag = false;

    public int _listen_port;

    public String _process_uuid;

    public boolean main(String[] args) {
        M365ProxyListenConnection m365_proxy_listen_connection = new M365ProxyListenConnection();
        m365_proxy_listen_connection.init(_listen_port, _process_uuid, _thread_pool_num);
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
                    _exit_flag = true;
                    return true;
                case "-v":
                    showVersion();
                    _exit_flag = true;
                    return true;
                case "-p":
                    if (arg.contains("-p")){
                        _listen_port = Integer.parseInt((arg.substring(2, arg.length())));
                    }
                    break;
                case "-u":
                    if (arg.contains("-u")){
                        _process_uuid = (arg.substring(2, arg.length()));
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
        return _exit_flag;
    }

    /**
     * @Description : global init
     * @return true
     */
    public boolean globalInit() {
        //init logpack
        M365ProxyConfig.initLogback();
        //init global exit flag
        M365ProxyGlobalVals.g_service_exit_flag = false;

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

        helpString += _app_name + ": Vinchin backup and recovery system\n";
        helpString += "\nUsage: " + _app_name + "[options]\n";
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

        version += _app_name + " v2.0\n";
        version += "Copyright (c) 2014-2023 Vinchin, Inc.";

        System.out.println(version);
    }
}
