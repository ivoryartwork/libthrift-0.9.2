package org.apache.thriftstudy.server;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public class TThreadedSelectorServer extends AbstractNonblockingServer {

    public TThreadedSelectorServer(Args args) {
        super(args);
    }


    protected class AcceptSelector extends Thread {
        
    }

    @Override
    protected boolean startThreads() {
        return false;
    }
}
