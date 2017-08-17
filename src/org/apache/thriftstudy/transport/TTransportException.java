package org.apache.thriftstudy.transport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TTransportException extends TException {

    public static final int NOT_OPEN = 1;
    private int type_;

    public TTransportException(String message) {
        super(message);
    }

    public TTransportException(Throwable t) {
        super(t);
    }

    public TTransportException(int type, String message) {
        super(message);
        type_ = type;
    }
}
