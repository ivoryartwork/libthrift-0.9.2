package org.apache.thriftstudy.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.channels.*;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TNonblockingServerSocket extends TNonblockingServerTransport {

    private ServerSocketChannel serverSocketChannel = null;

    //underlying server socket
    private ServerSocket serverSocket_;

    private int clientTimeout_;

    public static class NonblockingAbstractServerSocketArgs
            extends AbstractServerTransportArgs<NonblockingAbstractServerSocketArgs> {
    }

    public TNonblockingServerSocket(int port) throws TTransportException {
        this(port, 0);
    }

    public TNonblockingServerSocket(int port, int clientTimeout) throws TTransportException {
        this(new NonblockingAbstractServerSocketArgs().port(port).clientTimeout(clientTimeout));
    }

    public TNonblockingServerSocket(InetSocketAddress bindAddr) throws TTransportException {
        this(bindAddr, 0);
    }

    public TNonblockingServerSocket(InetSocketAddress bindAddr, int clientTimeout) throws TTransportException {
        this(new NonblockingAbstractServerSocketArgs().bindAddr(bindAddr).clientTimeout(clientTimeout));
    }

    public TNonblockingServerSocket(NonblockingAbstractServerSocketArgs args) throws TTransportException {
        clientTimeout_ = args.clientTimeout;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            serverSocket_ = serverSocketChannel.socket();
            //When a TCP connection is closed the connection may remain in a timeout state
            //for a period of time after the connection is closed
            //(typically known as the TIME_WAIT state or 2MSL wait state).
            //For applications using a well known socket address or port it may not
            //be possible to bind a socket to the required SocketAddress
            //if there is a connection in the timeout state involving the socket address or port
            serverSocket_.setReuseAddress(true);

            serverSocket_.bind(args.bindAddr, args.backlog);
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket_ = null;
            throw new TTransportException("Cloud not create ServerSocket on address " + args.bindAddr.toString() + ".");
        }
    }

    @Override
    public void registerSelector(Selector selector) {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listen() throws TTransportException {
        if (serverSocket_ != null) {
            try {
                serverSocket_.setSoTimeout(0);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected TTransport acceptImpl() throws TTransportException {
        if (serverSocket_ == null) {
            throw new TTransportException(TTransportException.NOT_OPEN, "No underlying server socket.");
        }
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                return null;
            }
            TNonblockingSocket tsocket = new TNonblockingSocket(socketChannel);
            tsocket.setTimeout(clientTimeout_);
            return tsocket;
        } catch (IOException e) {
            throw new TTransportException(e);
        }
    }

    @Override
    public void close() {
        if (serverSocket_ != null) {
            try {
                serverSocket_.close();
            } catch (IOException e) {
                System.out.println("WARNING: Could not close server socket: " + e.getMessage());
            }
        }
        serverSocket_ = null;
    }
}
