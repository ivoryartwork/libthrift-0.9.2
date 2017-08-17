package org.apache.thriftstudy.transport;

import java.io.Closeable;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class TTransport implements Closeable {

    /**
     * Closes the transport.
     */
    public abstract void close();
}
