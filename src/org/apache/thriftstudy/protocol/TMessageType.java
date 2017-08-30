package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/30
 */
public final class TMessageType {
    public static final byte CALL = 1;
    public static final byte REPLY = 2;
    public static final byte EXCEPTION = 3;
    public static final byte ONEWAY = 4;
}
