package org.apache.thriftstudy.transport;

import java.nio.channels.SocketChannel;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TNonblockingSocket extends TNonblockingTransport {

    public TNonblockingSocket(SocketChannel socketChannel) {

    }

    public void setTimeout(int timeout){

    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {

    }
}