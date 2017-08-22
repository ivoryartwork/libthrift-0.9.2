package org.apache.thriftstudy.protocol;

import org.apache.thriftstudy.TException;
import org.apache.thriftstudy.transport.TTransport;

import java.nio.ByteBuffer;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/21
 */
public abstract class TProtocol {

    private TProtocol() {
    }

    protected TTransport trans_;

    protected TProtocol(TTransport transport) {
        trans_ = transport;
    }

    public TTransport getTransport() {
        return trans_;
    }

    /**
     * Writing methods.
     */

    public abstract void writeMessageBegin(TMessage message) throws TException;

    public abstract void writeMessageEnd() throws TException;

    public abstract void writeStructBegin(TStruct struct) throws TException;

    public abstract void writeStructEnd() throws TException;

    public abstract void writeFieldBegin(TField field) throws TException;

    public abstract void writeFieldEnd() throws TException;

    public abstract void writeFieldStop() throws TException;

    public abstract void writeMapBegin(TMap map) throws TException;

    public abstract void writeMapEnd() throws TException;

    public abstract void writeListBegin(TList list) throws TException;

    public abstract void writeListEnd() throws TException;

    public abstract void writeSetBegin(TSet set) throws TException;

    public abstract void writeSetEnd() throws TException;

    public abstract void writeBool(boolean b) throws TException;

    public abstract void writeByte(byte b) throws TException;

    public abstract void writeI16(short i16) throws TException;

    public abstract void writeI32(int i32) throws TException;

    public abstract void writeI64(long i64) throws TException;

    public abstract void writeDouble(double dub) throws TException;

    public abstract void writeString(String str) throws TException;

    public abstract void writeBinary(ByteBuffer buf) throws TException;

    /**
     * Reading methods.
     */

    public abstract TMessage readMessageBegin() throws TException;

    public abstract void readMessageEnd() throws TException;

    public abstract TStruct readStructBegin() throws TException;

    public abstract void readStructEnd() throws TException;

    public abstract TField readFieldBegin() throws TException;

    public abstract void readFieldEnd() throws TException;

    public abstract TMap readMapBegin() throws TException;

    public abstract void readMapEnd() throws TException;

    public abstract TList readListBegin() throws TException;

    public abstract void readListEnd() throws TException;

    public abstract TSet readSetBegin() throws TException;

    public abstract void readSetEnd() throws TException;

    public abstract boolean readBool() throws TException;

    public abstract byte readByte() throws TException;

    public abstract short readI16() throws TException;

    public abstract int readI32() throws TException;

    public abstract long readI64() throws TException;

    public abstract double readDouble() throws TException;

    public abstract String readString() throws TException;

    public abstract ByteBuffer readBinary() throws TException;
}
