package org.apache.thriftstudy.server;

/**
 * 提供Nonblocking TServer共同的方法和类的实现
 *
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class AbstractNonblockingServer extends TServer {


    public AbstractNonblockingServer(Args args) {
        super(args);
    }

    /**
     * 启动服务启动时需要启动的所有线程
     *
     * @return true如果所有线程都成功启动，false如果线程启动失败
     */
    protected abstract boolean startThreads();

    /**
     * 开始接受连接和处理rpc调用
     */
    @Override
    public void serve() {
        if (!startThreads()) {
            return;
        }
    }
}
