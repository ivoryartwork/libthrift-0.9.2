package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/30
 */
public class TMultiplexedProtocol extends TProtocolDecorator {

    /** Used to delimit the service name from the function name */
    public static final String SEPARATOR = ":";

    public TMultiplexedProtocol(TProtocol protocol) {
        super(protocol);
    }
}
