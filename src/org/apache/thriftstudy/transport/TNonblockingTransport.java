package org.apache.thriftstudy.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public abstract class TNonblockingTransport extends TTransport {

    public abstract boolean startConnect() throws IOException;

    public abstract boolean finishConnect() throws IOException;

    public abstract SelectionKey registerSelector(Selector selector, int interestOps) throws IOException;

    public abstract int read(ByteBuffer buffer) throws IOException;

    public abstract int write(ByteBuffer buffer) throws IOException;
}
