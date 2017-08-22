package org.apache.thriftstudy.protocol;

import org.apache.thriftstudy.TException;
import org.apache.thriftstudy.transport.TTransport;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public class TBinaryProtocol extends TProtocol {

    protected boolean strictWrite_;
    protected boolean strictRead_;
    protected static final int VERSION_1 = 0x80010000;
    private static final long NO_LENGTH_LIMIT = -1;
    private final long stringLengthLimit_;
    private final long containerLengthLimit_;


    /**
     * Factory
     */
    public static class Factory implements TProtocolFactory {
        protected long stringLengthLimit_;
        protected long containerLengthLimit_;
        protected boolean strictRead_;
        protected boolean strictWrite_;

        public Factory() {
            this(false, true);
        }

        public Factory(boolean strictRead, boolean strictWrite) {
            this(strictRead, strictWrite, NO_LENGTH_LIMIT, NO_LENGTH_LIMIT);
        }

        public Factory(boolean strictRead, boolean strictWrite, long stringLengthLimit, long containerLengthLimit) {
            stringLengthLimit_ = stringLengthLimit;
            containerLengthLimit_ = containerLengthLimit;
            strictRead_ = strictRead;
            strictWrite_ = strictWrite;
        }

        public TProtocol getProtocol(TTransport trans) {
            return new TBinaryProtocol(trans, stringLengthLimit_, containerLengthLimit_, strictRead_, strictWrite_);
        }
    }

    protected TBinaryProtocol(TTransport transport) {
        this(transport, false, true);
    }

    public TBinaryProtocol(TTransport transport, boolean strictRead, boolean strictWrite) {
        this(transport, NO_LENGTH_LIMIT, NO_LENGTH_LIMIT, strictRead, strictWrite);
    }

    public TBinaryProtocol(TTransport transport, long stringLengthLimit, long containerLengthLimit, boolean strictRead, boolean strictWrite) {
        super(transport);
        strictRead_ = strictRead;
        strictWrite_ = strictWrite;
        stringLengthLimit_ = stringLengthLimit;
        containerLengthLimit_ = containerLengthLimit;
    }

    /**
     * Writing methods.
     *
     * @param message
     */
    @Override
    public void writeMessageBegin(TMessage message) throws TException {
        if (strictWrite_) {
            int version = VERSION_1 | message.type;
            writeI32(version);
            writeString(message.name);
            writeI32(message.seqid);
        } else {
            writeString(message.name);
            writeI32(message.type);
            writeI32(message.seqid);
        }
    }

    @Override
    public void writeMessageEnd() throws TException {

    }

    @Override
    public void writeStructBegin(TStruct struct) throws TException {

    }

    @Override
    public void writeStructEnd() throws TException {

    }

    @Override
    public void writeFieldBegin(TField field) throws TException {
        writeByte(field.type);
        writeI64(field.id);
    }

    @Override
    public void writeFieldEnd() throws TException {

    }

    @Override
    public void writeFieldStop() throws TException {
        writeByte(TType.STOP);
    }

    @Override
    public void writeMapBegin(TMap map) throws TException {
        writeByte(map.keyType);
        writeByte(map.valueType);
        writeI32(map.size);
    }

    @Override
    public void writeMapEnd() throws TException {

    }

    @Override
    public void writeListBegin(TList list) throws TException {
        writeByte(list.elemType);
        writeI32(list.size);
    }

    @Override
    public void writeListEnd() throws TException {

    }

    @Override
    public void writeSetBegin(TSet set) throws TException {
        writeByte(set.elemType);
        writeI32(set.size);
    }

    @Override
    public void writeSetEnd() throws TException {

    }

    @Override
    public void writeBool(boolean b) throws TException {
        writeByte(b ? (byte) 1 : (byte) 0);
    }

    private byte[] bout = new byte[1];

    @Override
    public void writeByte(byte b) throws TException {
        bout[0] = b;
        trans_.write(bout, 0, 1);
    }

    private byte[] i16out = new byte[2];

    @Override
    public void writeI16(short i16) throws TException {
        i16out[0] = (byte) (0xff & (i16 >> 8));
        i16out[1] = (byte) (0xff & (i16));
        trans_.write(i16out, 0, 2);
    }

    private byte[] i32out = new byte[4];

    @Override
    public void writeI32(int i32) throws TException {
        i32out[0] = (byte) (0xff & (i32 >> 24));
        i32out[1] = (byte) (0xff & (i32 >> 16));
        i32out[2] = (byte) (0xff & (i32 >> 8));
        i32out[3] = (byte) (0xff & (i32));
        trans_.write(i32out, 0, 4);
    }

    private byte[] i64out = new byte[8];

    @Override
    public void writeI64(long i64) throws TException {
        i64out[0] = (byte) (0xff & (i64 >> 56));
        i64out[1] = (byte) (0xff & (i64 >> 48));
        i64out[2] = (byte) (0xff & (i64 >> 40));
        i64out[3] = (byte) (0xff & (i64 >> 32));
        i64out[4] = (byte) (0xff & (i64 >> 24));
        i64out[5] = (byte) (0xff & (i64 >> 16));
        i64out[6] = (byte) (0xff & (i64 >> 8));
        i64out[7] = (byte) (0xff & (i64));
        trans_.write(i64out, 0, 8);
    }

    @Override
    public void writeDouble(double dub) throws TException {
        writeI64(Double.doubleToLongBits(dub));
    }

    @Override
    public void writeString(String str) throws TException {
        try {
            byte[] dat = str.getBytes("UTF-8");
            writeI32(dat.length);
            trans_.write(dat, 0, dat.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    @Override
    public void writeBinary(ByteBuffer buf) throws TException {
        int length = buf.limit() - buf.position();
        writeI32(length);
        trans_.write(buf.array(), buf.position() + buf.position(), length);
    }

    /**
     * Reading methods.
     */
    @Override
    public TMessage readMessageBegin() throws TException {

        return null;
    }

    @Override
    public void readMessageEnd() throws TException {

    }

    @Override
    public TStruct readStructBegin() throws TException {
        return null;
    }

    @Override
    public void readStructEnd() throws TException {

    }

    @Override
    public TField readFieldBegin() throws TException {
        return null;
    }

    @Override
    public void readFieldEnd() throws TException {

    }

    @Override
    public TMap readMapBegin() throws TException {
        return null;
    }

    @Override
    public void readMapEnd() throws TException {

    }

    @Override
    public TList readListBegin() throws TException {
        return null;
    }

    @Override
    public void readListEnd() throws TException {

    }

    @Override
    public TSet readSetBegin() throws TException {
        return null;
    }

    @Override
    public void readSetEnd() throws TException {

    }

    @Override
    public boolean readBool() throws TException {
        return (readByte() == 1);
    }

    private byte[] bin = new byte[1];

    @Override
    public byte readByte() throws TException {
        if (trans_.getBytesRemainingInBuffer() >= 1) {
            byte b = trans_.getBuffer()[trans_.getBufferPosition()];
            trans_.consumeBuffer(1);
            return b;
        }
        readAll(bin, 0, 1);
        return bin[0];
    }

    private byte[] i16rd = new byte[4];

    @Override
    public short readI16() throws TException {
        byte[] buf = i16rd;
        int off = 0;
        if (trans_.getBytesRemainingInBuffer() >= 2) {
            buf = trans_.getBuffer();
            off = trans_.getBufferPosition();
            trans_.consumeBuffer(1);
        } else {
            readAll(buf, 0, 2);
        }
        return (short) (((buf[off] & 0xff) << 8) |
                buf[off + 1] & 0xff);
    }

    private byte[] i32rd = new byte[4];

    @Override
    public int readI32() throws TException {
        byte[] buf = i32rd;
        int off = 0;
        if (trans_.getBytesRemainingInBuffer() >= 4) {
            buf = trans_.getBuffer();
            off = trans_.getBufferPosition();
            trans_.consumeBuffer(4);
        } else {
            readAll(i32rd, 0, 4);
        }

        return ((buf[off] & 0xff) << 24) |
                ((buf[off + 1] & 0xff) << 16) |
                ((buf[off + 2] & 0xff) << 8) |
                ((buf[off + 3] & 0xff));
    }

    private byte[] i64rd = new byte[8];

    @Override
    public long readI64() throws TException {
        byte[] buf = i64rd;
        int off = 0;
        if (trans_.getBytesRemainingInBuffer() >= 8) {
            buf = trans_.getBuffer();
            off = trans_.getBufferPosition();
            trans_.consumeBuffer(8);
        } else {
            readAll(i64rd, 0, 8);
        }
        return ((long) (buf[off] & 0xff) << 56) |
                ((long) (buf[off + 1] & 0xff) << 48) |
                ((long) (buf[off + 2] & 0xff) << 40) |
                ((long) (buf[off + 3] & 0xff) << 32) |
                ((long) (buf[off + 4] & 0xff) << 24) |
                ((long) (buf[off + 5] & 0xff) << 16) |
                ((long) (buf[off + 6] & 0xff) << 8) |
                ((long) (buf[off + 7] & 0xff));
    }

    @Override
    public double readDouble() throws TException {
        return Double.longBitsToDouble(readI64());
    }

    @Override
    public String readString() throws TException {
        int size = readI32();
        checkStringReadLength(size);
        if (stringLengthLimit_ > 0 && size > stringLengthLimit_) {
            throw new TProtocolException(TProtocolException.SIZE_LIMIT,
                    "String field exceeded string size limit");
        }
        if (trans_.getBytesRemainingInBuffer() >= size) {
            try {
                String s = new String(trans_.getBuffer(), trans_.getBufferPosition(), size, "UTF-8");
                trans_.consumeBuffer(size);
                return s;
            } catch (UnsupportedEncodingException e) {
                throw new TException("JVM DOES NOT SUPPORT UTF-8");
            }
        }
        return readStringBody(size);
    }

    @Override
    public ByteBuffer readBinary() throws TException {
        int size = readI32();
        if (stringLengthLimit_ > 0 && size > stringLengthLimit_) {
            throw new TProtocolException(TProtocolException.SIZE_LIMIT,
                    "Binary field exceeded string size limit");
        }
        if (trans_.getBytesRemainingInBuffer() >= size) {
            ByteBuffer bb = ByteBuffer.wrap(trans_.getBuffer(), trans_.getBufferPosition(), size);
            trans_.consumeBuffer(size);
            return bb;
        }

        byte[] buf = new byte[size];
        trans_.readAll(buf, 0, size);
        return ByteBuffer.wrap(buf);
    }

    public String readStringBody(int size) throws TException {
        try {
            byte[] buf = new byte[size];
            trans_.readAll(buf, 0, size);
            return new String(buf, "UTF-8");
        } catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT UTF-8");
        }
    }

    private void checkStringReadLength(int length) throws TProtocolException {
        if (length < 0) {
            throw new TProtocolException(TProtocolException.NEGATIVE_SIZE,
                    "Negative length: " + length);
        }
        if (stringLengthLimit_ != NO_LENGTH_LIMIT && length > stringLengthLimit_) {
            throw new TProtocolException(TProtocolException.SIZE_LIMIT,
                    "Length exceeded max allowed: " + length);
        }
    }

    private int readAll(byte[] buf, int off, int len) throws TException {
        return trans_.readAll(buf, off, len);
    }
}
