package org.apache.thriftstudy.transport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TException extends Exception {

    public TException(String msg) {
        super(msg);
    }

    public TException(Throwable t) {
        super(t);
    }
}
