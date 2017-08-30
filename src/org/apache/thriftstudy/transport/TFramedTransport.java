package org.apache.thriftstudy.transport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/30
 */
public class TFramedTransport extends TTransport {


    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void open() throws TTransportException {

    }

    @Override
    public void close() {

    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {

    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        return 0;
    }
}
