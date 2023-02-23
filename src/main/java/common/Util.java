/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	Util.java: Common tools
 * Author		:	yangjunjie
 * Date			:	2023/02/23
 * Modify		:
 *
 *
 ***********************************************************************/

package common;

import java.util.UUID;

public class Util {
    public static String genericUuid(){
        return UUID.randomUUID().toString();
    }
}
