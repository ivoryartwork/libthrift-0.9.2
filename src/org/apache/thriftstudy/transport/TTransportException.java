package org.apache.thriftstudy.transport;

import org.apache.thriftstudy.TException;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TTransportException extends TException {

    private static final long serialVersionUID = 1L;

    public static final int UNKNOWN = 0;
    public static final int NOT_OPEN = 1;
    public static final int ALREADY_OPEN = 2;
    public static final int TIMED_OUT = 3;
    public static final int END_OF_FILE = 4;

    protected int type_ = UNKNOWN;

    public TTransportException() {
        super();
    }

    public TTransportException(int type) {
        super();
        type_ = type;
    }

    public TTransportException(int type, String message) {
        super(message);
        type_ = type;
    }

    public TTransportException(String message) {
        super(message);
    }

    public TTransportException(int type, Throwable cause) {
        super(cause);
        type_ = type;
    }

    public TTransportException(Throwable cause) {
        super(cause);
    }

    public TTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TTransportException(int type, String message, Throwable cause) {
        super(message, cause);
        type_ = type;
    }

    public int getType() {
        return type_;
    }
}
