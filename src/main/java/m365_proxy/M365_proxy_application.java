/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	m365_proxy_application.java:
 * Author		:	yangjunjie
 * Date			:	2023/02/17
 * Modify		:
 *
 *
 ***********************************************************************/

package m365_proxy;

import java.io.IOException;

public class M365_proxy_application {

    String _app_name = "M365_proxy";

    int _thread_pool_num = 30;

    public boolean _exit_flag = false;

    public int _listen_port;

    public String _process_uuid;

    public Boolean main(String[] args) throws IOException {
        boolean ret = true;

        M365_proxy_listen_connection m365_proxy_listen_connection = new M365_proxy_listen_connection();
        m365_proxy_listen_connection.init(_listen_port, _process_uuid, _thread_pool_num);
        ret = m365_proxy_listen_connection.run();
        m365_proxy_listen_connection.destroy();

        return ret;
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

        //init global exit flag
        M365_proxy_global_vals.g_service_exit_flag = false;

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
