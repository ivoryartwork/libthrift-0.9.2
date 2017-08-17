package org.apache.thriftstudy.server;

import org.apache.thriftstudy.TProcessorFactory;
import org.apache.thriftstudy.protocol.TProtocolFactory;
import org.apache.thriftstudy.transport.TServerTransport;
import org.apache.thriftstudy.transport.TTransportFactory;

/**
 * Thrift server 的共有接口
 *
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class TServer {

    public static class Args extends AbstractServerArgs<Args> {

        public Args(TServerTransport transport) {
            super(transport);
        }
    }

    public static abstract class AbstractServerArgs<T extends AbstractServerArgs<T>> {
        final TServerTransport serverTransport;
        TProcessorFactory processorFactory;
        TTransportFactory inputTransportFactory;
        TTransportFactory outputTransportFactory;
        TProtocolFactory inputProtocolFactory;
        TProtocolFactory outProtocolFactory;

        public AbstractServerArgs(TServerTransport transport) {
            serverTransport = transport;
        }


    }

    /**
     * server transport
     */
    private TServerTransport serverTransport_;

    /**
     * Core processor
     */
    private TProcessorFactory processorFactory_;

    /**
     * Input Transport Factory
     */
    private TTransportFactory inputTransportFactory_;

    /**
     * Output Transport Factory
     */
    private TTransportFactory outputTransportFactory_;

    /**
     * Input Protocol Factory
     */
    private TProtocolFactory inputProtocolFactory_;

    /**
     * Output Protocol Factory
     */
    private TProtocolFactory outputProtocolFactory_;


    public TServer(Args args) {
        processorFactory_ = args.processorFactory;
        serverTransport_ = args.serverTransport;
        inputTransportFactory_ = args.inputTransportFactory;
        outputTransportFactory_ = args.outputTransportFactory;
        inputProtocolFactory_ = args.inputProtocolFactory;
        outputProtocolFactory_ = args.outProtocolFactory;
    }

    /**
     * 该方法用来启动rpc服务
     */
    public abstract void serve();
}
