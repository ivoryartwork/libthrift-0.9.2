package org.apache.thriftstudy;


import org.apache.thriftstudy.protocol.TBinaryProtocol;
import org.apache.thriftstudy.server.TThreadedSelectorServer;
import org.apache.thriftstudy.transport.TNonblockingServerSocket;
import org.apache.thriftstudy.transport.TTransportException;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/18
 */
public class TThreadedSelectorServerTest {

    public static void main(String[] args1) {
        try {
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(8888);
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport);
            TMultiplexedProcessor processor = new TMultiplexedProcessor();
            TProcessorFactory processorFactory = new TProcessorFactory(processor);
            args.processorFactory(processorFactory);
            args.protocolFactory(new TBinaryProtocol.Factory(false, true));
            TThreadedSelectorServer server = new TThreadedSelectorServer(args);
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}