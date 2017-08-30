package org.apache.thriftstudy.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    public TNonblockingSocket(String host, int port) throws IOException {
        this(host, port, 0);
    }

    public TNonblockingSocket(String host, int port, int timeout) throws IOException {
        this(SocketChannel.open(), timeout, new InetSocketAddress(host, port));
    }

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

    @Override
    public boolean isOpen() {
        return socketChannel_.isOpen() && socketChannel_.isConnected();
    }

    @Override
    public void open() throws TTransportException {
        throw new TTransportException("open() is not implemented for TNonblockingSocket");
    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {
        try {
            socketChannel_.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("关闭连接失败");
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        if ((socketChannel_.validOps() & SelectionKey.OP_WRITE) != SelectionKey.OP_WRITE) {
            throw new TTransportException("Cannot write from read-only socket channel");
        }
        try {
            socketChannel_.write(ByteBuffer.wrap(buf, off, len));
        } catch (IOException e) {
            throw new TTransportException(TTransportException.UNKNOWN, e);
        }
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        if ((socketChannel_.validOps() & SelectionKey.OP_READ) != SelectionKey.OP_READ) {
            throw new TTransportException("Cannot read from write-only socket channel");
        }
        try {
            return socketChannel_.read(ByteBuffer.wrap(buf, off, len));
        } catch (IOException e) {
            throw new TTransportException(TTransportException.UNKNOWN, e);
        }
    }

    @Override
    public boolean startConnect() throws IOException {
        return socketChannel_.connect(socketAddress_);
    }

    /**
     * 非阻塞模式下判断是否完成连接
     *
     * @return
     * @throws IOException
     */
    @Override
    public boolean finishConnect() throws IOException {
        return socketChannel_.finishConnect();
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