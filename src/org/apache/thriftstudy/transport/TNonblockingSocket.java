package org.apache.thriftstudy.transport;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TNonblockingSocket extends TNonblockingTransport {

    private final SocketAddress socketAddress_;
    private final SocketChannel socketChannel_;

    public TNonblockingSocket(SocketChannel socketChannel) throws IOException {
        this(socketChannel, 0, null);
    }

    public TNonblockingSocket(SocketChannel socketChannel, int timeout, SocketAddress socketAddress) throws IOException {
        socketAddress_ = socketAddress;
        socketChannel_ = socketChannel;

        socketChannel.configureBlocking(false);

        Socket socket = socketChannel.socket();
        socket.setSoLinger(false, 0);
        socket.setTcpNoDelay(true);
        socket.setKeepAlive(true);
        setTimeout(timeout);
    }

    public void setTimeout(int timeout) {
        try {
            socketChannel_.socket().setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {

    }

    @Override
    public SelectionKey registerSelector(Selector selector, int interestOps) throws IOException {
        return socketChannel_.register(selector, interestOps);
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException {
        return socketChannel_.read(buffer);
    }

    @Override
    public int write(ByteBuffer buffer) throws IOException {
        return socketChannel_.write(buffer);
    }
}