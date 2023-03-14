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

package com.vinchin.m365proxy.common;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

public class Util {
    public static String genericUuid(){
        return UUID.randomUUID().toString();
    }

    public static boolean jsonStringIsEmpty(String jsonString){
        if (jsonString == "{}" || jsonString == "[]"){
            return true;
        }

        return false;
    }

    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }
}
