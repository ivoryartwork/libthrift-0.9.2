package org.apache.thriftstudy.transport;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class TServerTransport implements Closeable {

    public static abstract class AbstractServerTransportArgs<T extends AbstractServerTransportArgs<T>> {
        //已经完成三次握手的socket队列大小
        int backlog = 0;
        int clientTimeout = 0;
        InetSocketAddress bindAddr;

        public T backlog(int backlog) {
            this.backlog = backlog;
            return (T) this;
        }

        public T clientTimeout(int clientTimeout) {
            this.clientTimeout = clientTimeout;
            return (T) this;
        }

        public T port(int port) {
            this.bindAddr = new InetSocketAddress(port);
            return (T) this;
        }

        public T bindAddr(InetSocketAddress bindAddr) {
            this.bindAddr = bindAddr;
            return (T) this;
        }
    }

    public abstract void listen() throws TTransportException;

    /**
     * 接受客户端的连接
     *
     * @throws TTransportException
     */
    public final TTransport accept() throws TTransportException {
        TTransport transport = acceptImpl();
        if (transport == null) {
            throw new TTransportException("accept() may not return NULL");
        }
        return transport;
    }

    protected abstract TTransport acceptImpl() throws TTransportException;

    public abstract void close();

    /**
     * 中断
     */
    public void interrupt() {
    }
}
