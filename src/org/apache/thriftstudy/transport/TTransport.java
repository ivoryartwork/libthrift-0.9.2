package org.apache.thriftstudy.transport;

import java.io.Closeable;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class TTransport implements Closeable {

    public abstract boolean isOpen();

    public boolean peek() {
        return isOpen();
    }

    public abstract void open() throws TTransportException;

    /**
     * Closes the transport.
     */
    public abstract void close();

    public void write(byte buf[]) throws TTransportException {
        write(buf, 0, buf.length);
    }

    public abstract void write(byte buf[], int off, int len) throws TTransportException;

    public abstract int read(byte[] buf, int off, int len) throws TTransportException;

    public int readAll(byte[] buf, int off, int len) throws TTransportException {
        int got = 0;
        int ret = 0;
        while (got < len) {
            ret = read(buf, off + got, len - got);
            if (ret <= 0) {
                throw new TTransportException(
                        "Cannot read. Remote side has closed. Tried to read "
                                + len
                                + " bytes, but only got "
                                + got
                                + " bytes. (This is often indicative of an internal error on the server side. Please check your server logs.)");
            }
            got += ret;
        }
        return got;
    }

    public void flush() throws TTransportException {
    }

    public byte[] getBuffer() {
        return null;
    }

    public int getBufferPosition() {
        return 0;
    }

    public int getBytesRemainingInBuffer() {
        return -1;
    }

    public void consumeBuffer(int len) {
    }
}
