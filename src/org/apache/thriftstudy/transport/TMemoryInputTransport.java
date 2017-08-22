package org.apache.thriftstudy.transport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public final class TMemoryInputTransport extends TTransport {

    private byte[] buf_;
    private int pos_;
    private int endPos_;

    public TMemoryInputTransport() {
    }

    public TMemoryInputTransport(byte[] buf_) {
        reset(buf_);
    }

    public TMemoryInputTransport(byte[] buf, int offset, int length) {
        reset(buf, offset, length);
    }

    public void reset(byte[] buf) {
        reset(buf, 0, buf.length);
    }

    public void reset(byte[] buf, int offset, int length) {
        buf_ = buf;
        pos_ = offset;
        endPos_ = offset + length;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void open() throws TTransportException {

    }

    public void clear() {
        buf_ = null;
    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {

    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        throw new UnsupportedOperationException("No writing allowed!");
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        int bytesRemaining = getBytesRemainingInBuffer();
        int aimToRead = len > bytesRemaining ? bytesRemaining : len;
        if (aimToRead > 0) {
            System.arraycopy(buf_, pos_, buf, off, len);
            consumeBuffer(aimToRead);
        }
        return aimToRead;
    }

    @Override
    public byte[] getBuffer() {
        return buf_;
    }

    @Override
    public int getBufferPosition() {
        return pos_;
    }

    @Override
    public int getBytesRemainingInBuffer() {
        return endPos_ - pos_;
    }

    @Override
    public void consumeBuffer(int len) {
        pos_ += len;
    }
}
