package org.apache.thriftstudy.transport;

import java.io.ByteArrayOutputStream;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public class TByteArrayOutputStream extends ByteArrayOutputStream {

    private final int initialSize;

    public TByteArrayOutputStream() {
        this(32);
    }

    public TByteArrayOutputStream(int size) {
        super(size);
        this.initialSize = size;
    }

    public void reset() {
        super.reset();
        if (buf.length > initialSize) {
            buf = new byte[initialSize];
        }
    }

    public byte[] get() {
        return buf;
    }

    public int len() {
        return count;
    }
}
