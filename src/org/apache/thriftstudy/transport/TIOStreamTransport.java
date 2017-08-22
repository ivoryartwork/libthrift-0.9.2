package org.apache.thriftstudy.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public class TIOStreamTransport extends TTransport {

    protected InputStream inputStream_ = null;
    protected OutputStream outputStream_ = null;

    public TIOStreamTransport() {
    }

    public TIOStreamTransport(InputStream inputStream) {
        this.inputStream_ = inputStream;
    }

    public TIOStreamTransport(OutputStream outputStream) {
        this.outputStream_ = outputStream;
    }

    public TIOStreamTransport(InputStream inputStream, OutputStream outputStream) {
        this.inputStream_ = inputStream;
        this.outputStream_ = outputStream;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void open() throws TTransportException {

    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {
        if (inputStream_ != null) {
            try {
                inputStream_.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        inputStream_ = null;
        if (outputStream_ != null) {
            try {
                outputStream_.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputStream_ = null;
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        if (outputStream_ == null) {
            throw new TTransportException(TTransportException.NOT_OPEN, "Cannot write from null outputStream");
        }
        try {
            outputStream_.write(buf, off, len);
        } catch (IOException e) {
            throw new TTransportException(TTransportException.UNKNOWN, e);
        }

    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (inputStream_ == null) {
            throw new TTransportException(TTransportException.NOT_OPEN, "Cannot read from null inputStream");
        }
        int bytesRead = 0;
        try {
            bytesRead = inputStream_.read(buf, off, len);
        } catch (IOException e) {
            throw new TTransportException(TTransportException.UNKNOWN, e);
        }
        if (bytesRead < 0) {
            throw new TTransportException(TTransportException.END_OF_FILE);
        }
        return 0;
    }

    /**
     * Flushes the underlying output stream if not null.
     */
    public void flush() throws TTransportException {
        if (outputStream_ == null) {
            throw new TTransportException(TTransportException.NOT_OPEN, "Cannot flush null outputStream");
        }
        try {
            outputStream_.flush();
        } catch (IOException iox) {
            throw new TTransportException(TTransportException.UNKNOWN, iox);
        }
    }
}
