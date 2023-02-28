/**********************************************************************
 *
 *
 * Copyright (c) 2014-2023 Vinchin, Inc. All rights reserved.
 *
 * Description	:	TypeConversion.java: definition of some marco and struct
 * Author		:	yangjunjie
 * Date			:	2023/02/20
 * Modify		:
 *
 *
 ***********************************************************************/

package common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TypeConversion {

    /**
     * C++'s small-end byte is converted to Java's large-end int type
     * @param byteVal
     * @return
     */
    public static int bytesToInt(byte[] byteVal) {
        byte[] tmp = new byte[4];
        System.arraycopy(byteVal, 0, tmp, 0, byteVal.length);
        if (byteVal == null) return 0;
        /*
        	&0xff 的原因是将它原内容转为int再做位移和或运算
        	强转int由于java没有unsigned，所以byte是带符号位的
        	当单byte>128的时候强转int会出现负数(符号扩展)
        	最好的办法就是&0xff
        */
        return tmp[0] & 0xff | ((tmp[1] & 0xff) << 8) | ((tmp[2] & 0xff) << 16) | ((tmp[3] & 0xff) << 24);
    }

    public static byte[] intToBytes(int n){
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * @Description C++'s small-end byte is converted to Java's large-end long type
     * @param byteVal
     * @return
     */
    public static long bytesToLong(byte[] byteVal)  {
        int bytes = byteVal.length;

        switch(bytes) {
            case 0:
                return 0;
            case 1:
                return (long)((byteVal[0] & 0xff));
            case 2:
                return (long)((byteVal[0] & 0xff) | (byteVal[1] & 0xff)) << 8;
            case 3:
                return (long)((byteVal[0] & 0xff) | (byteVal[1] & 0xff) << 8 | (byteVal[2] & 0xff)) << 16 ;
            case 4:
                return (long)((byteVal[0] & 0xffL) | (byteVal[1] & 0xffL) << 8 | (byteVal[2] & 0xffL) << 16 | (byteVal[3] & 0xffL) << 24);
            case 5:
                return (long)((byteVal[0] & 0xffL) | (byteVal[1] & 0xffL) << 8 | (byteVal[2] & 0xffL) << 16 | (byteVal[3] & 0xffL) << 24 | (byteVal[4] & 0xffL) << 32);
            case 6:
                return (long)((byteVal[0] & 0xffL) | (byteVal[1] & 0xffL) << 8 | (byteVal[2] & 0xffL) << 16 | (byteVal[3] & 0xffL) << 24 | (byteVal[4] & 0xffL) << 32 | (byteVal[5] & 0xffL) << 40);
            case 7:
                return (long)((byteVal[0] & 0xffL) | (byteVal[1] & 0xffL) << 8 | (byteVal[2] & 0xffL) << 16 | (byteVal[3] & 0xffL) << 24 | (byteVal[4] & 0xffL) << 32 | (byteVal[5] & 0xffL) << 40 | (byteVal[6] & 0xffL) << 48);
            case 8:
                return (long)((byteVal[0] & 0xffL) | (byteVal[1] & 0xffL) << 8 | (byteVal[2] & 0xffL) << 16 | (byteVal[3] & 0xffL) << 24 | (byteVal[4] & 0xffL) << 32 | (byteVal[5] & 0xffL) << 40 | (byteVal[6] & 0xffL) << 48 | (byteVal[7] & 0xffL) << 56);
            default:
                return 0;
        }
    }

    public static byte[] longToBytes(long longNum) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (longNum >> 56);
        bytes[6] = (byte) (longNum >> 48);
        bytes[5] = (byte) (longNum >> 40);
        bytes[4] = (byte) (longNum >> 32);
        bytes[3] = (byte) (longNum >> 24);
        bytes[2] = (byte) (longNum >> 16);
        bytes[1] = (byte) (longNum >> 8);
        bytes[0] = (byte) longNum;
        return bytes;
    }

    public static String byteBufferToString(ByteBuffer byteBuffer) {
        String byteBufferString;
        try {
            byteBufferString = new String(byteBuffer.array(), StandardCharsets.UTF_8);
        } catch (Exception e){
            byteBufferString = "";
        }

        return byteBufferString;
    }

    public static byte[] stringToBytes(String string){
        byte[] bytes;

        try{
            bytes = string.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e){
            bytes = null;
        }

        return bytes;
    }
}
