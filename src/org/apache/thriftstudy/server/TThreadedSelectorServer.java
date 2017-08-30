package org.apache.thriftstudy.server;

import org.apache.thriftstudy.transport.TNonblockingServerTransport;
import org.apache.thriftstudy.transport.TNonblockingTransport;
import org.apache.thriftstudy.transport.TServerTransport;
import org.apache.thriftstudy.transport.TTransportException;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public class TThreadedSelectorServer extends AbstractNonblockingServer {

    //Flag for stopping the server
    private volatile boolean stopped_ = false;
    private final Args args;
    private ExecutorService invoker;
    private final Set<SelectorThread> selectorThreads = new HashSet<SelectorThread>();

    public TThreadedSelectorServer(Args args) {
        super(args);
        this.args = args;
    }

    public BlockingQueue<TNonblockingTransport> createDefaultAcceptQueue(int queueSize) {
        if (queueSize == 0) {
            return new LinkedBlockingQueue<TNonblockingTransport>();
        }
        return new ArrayBlockingQueue<TNonblockingTransport>(queueSize);
    }

    public static class Args extends AbstractNonblockingServerArgs<Args> {

        private AcceptPolicy acceptPolicy = AcceptPolicy.FAST_ACCEPT;
        private int selectorThreads = 2;
        private int acceptQueueSizePerThread = 4;

        public Args(TServerTransport transport) {
            super(transport);
        }

        public static enum AcceptPolicy {
            /**
             * Require accepted connection registration to be handled by the executor.
             * If the worker pool is saturated, further accepts will be closed
             * immediately. Slightly increases latency due to an extra scheduling.
             */
            FAIR_ACCEPT,
            /**
             * Handle the accepts as fast as possible, disregarding the status of the
             * executor service.
             */
            FAST_ACCEPT
        }
    }

    protected class AcceptThread extends Thread {
        private final TNonblockingServerTransport serverTransport;
        private final Selector acceptSelector;
        private final SelectorThreadLoadBalancer threadChooser;

        public AcceptThread(TNonblockingServerTransport serverTransport,
                            SelectorThreadLoadBalancer threadChooser) throws IOException {
            this.serverTransport = serverTransport;
            this.threadChooser = threadChooser;
            this.acceptSelector = SelectorProvider.provider().openSelector();
            this.serverTransport.registerSelector(acceptSelector);
        }

        @Override
        public void run() {
            try {
                while (!stopped_) {
                    select();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    acceptSelector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            TThreadedSelectorServer.this.stop();
        }

        private void select() {
            try {
                acceptSelector.select();
                Iterator<SelectionKey> selectionKeys = acceptSelector.selectedKeys().iterator();
                while (!stopped_ && selectionKeys.hasNext()) {
                    SelectionKey key = selectionKeys.next();
                    selectionKeys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        handleAccept();
                    } else {
                        System.out.println("unexcept io ops " + key.interestOps());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleAccept() {
            final TNonblockingTransport client = doAccept();
            if (client != null) {
                final SelectorThread selectorThread = threadChooser.nextThread();
                if (args.acceptPolicy == Args.AcceptPolicy.FAST_ACCEPT || invoker == null) {
                    doAndAccept(selectorThread, client);
                } else {
                    try {
                        invoker.submit(new Runnable() {
                            @Override
                            public void run() {
                                doAndAccept(selectorThread, client);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        e.printStackTrace();
                        client.close();
                    }
                }
            }
        }

        private TNonblockingTransport doAccept() {
            try {
                return (TNonblockingTransport) serverTransport.accept();
            } catch (TTransportException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void doAndAccept(SelectorThread thread, TNonblockingTransport clinet) {
            if (!thread.addAcceptedConnection(clinet)) {
                clinet.close();
            }
        }
    }

    @Override
    public void stop() {

    }

    protected class SelectorThread extends AbstractSelectorThread {
        private final BlockingQueue<TNonblockingTransport> acceptedQueue;

        public SelectorThread() throws IOException {
            this(new LinkedBlockingQueue<TNonblockingTransport>());
        }

        public SelectorThread(int maxPendingAccepts) throws IOException {
            this(createDefaultAcceptQueue(maxPendingAccepts));
        }

        public SelectorThread(BlockingQueue<TNonblockingTransport> acceptedQueue) throws IOException {
            this.acceptedQueue = acceptedQueue;
        }

        @Override
        public void run() {
            while (!stopped_) {
                select();
            }
        }

        private void select() {
            try {
                selector.select();
                processAcceptedConnections();
                processInterestChanges();

                Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                while (!stopped_ && selectionKeys.hasNext()) {
                    SelectionKey key = selectionKeys.next();
                    selectionKeys.remove();

                    if (!key.isValid()) {
                        cleanupSelectionKey(key);
                        continue;
                    }
                    if (key.isReadable()) {
                        //read
                        handleRead(key);
                    } else if (key.isWritable()) {
                        //write
                        handleWrite(key);
                    } else {
                        System.out.println("Unexpected state in select! " + key.interestOps());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void processAcceptedConnections() {
            while (!stopped_) {
                TNonblockingTransport accepted = acceptedQueue.poll();
                if (accepted == null) {
                    break;
                }
                registerAccepted(accepted);
            }
        }

        private void processInterestChanges() {
            synchronized (selectInterestChanges) {
                for (FrameBuffer fb : selectInterestChanges) {
                    fb.requestSelectInterestChange();
                }
                selectInterestChanges.clear();
            }
        }

        protected FrameBuffer createFrameBuffer(final TNonblockingTransport transport,
                                                final SelectionKey selectionKey,
                                                final AbstractSelectorThread selectorThread) {
            return new FrameBuffer(transport, selectionKey, selectorThread);
        }

        private void registerAccepted(TNonblockingTransport accepted) {
            SelectionKey clientKey = null;
            try {
                clientKey = accepted.registerSelector(selector, SelectionKey.OP_READ);

                FrameBuffer frameBuffer = createFrameBuffer(accepted, clientKey, SelectorThread.this);

                clientKey.attach(frameBuffer);
            } catch (IOException e) {
                e.printStackTrace();
                if (clientKey != null) {
                    cleanupSelectionKey(clientKey);
                }
                accepted.close();
            }
        }

        public boolean addAcceptedConnection(TNonblockingTransport transport) {
            try {
                acceptedQueue.put(transport);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            selector.wakeup();
            return true;
        }
    }

    protected static class SelectorThreadLoadBalancer {

        private final Collection<? extends SelectorThread> threads;
        private Iterator<? extends SelectorThread> nextThreadIterator;

        public <T extends SelectorThread> SelectorThreadLoadBalancer(Collection<T> threads) {
            if (threads.isEmpty()) {
                throw new IllegalArgumentException("At last one selector thread is required");
            }
            this.threads = Collections.unmodifiableList(new ArrayList<T>(threads));
            this.nextThreadIterator = threads.iterator();
        }

        public SelectorThread nextThread() {
            if (!nextThreadIterator.hasNext()) {
                nextThreadIterator = threads.iterator();
            }
            return nextThreadIterator.next();
        }
    }

    protected SelectorThreadLoadBalancer createSelectorThreadLoadBalancer(Collection<? extends SelectorThread> threads) {
        return new SelectorThreadLoadBalancer(threads);
    }

    @Override
    protected boolean startThreads() {
        try {
            for (int i = 0; i < args.selectorThreads; i++) {
                selectorThreads.add(new SelectorThread(args.acceptQueueSizePerThread));
            }
            AcceptThread acceptThread = new AcceptThread((TNonblockingServerTransport) serverTransport_, createSelectorThreadLoadBalancer(selectorThreads));
            for (SelectorThread selectorThread : selectorThreads) {
                selectorThread.start();
            }
            acceptThread.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean requestInvoke(FrameBuffer frameBuffer) {
        Invocation invocation = getRunnable(frameBuffer);
        if (invoker != null) {
            try {
                invoker.submit(invocation);
                return true;
            } catch (Exception e) {
                System.out.println("ExecutorService rejected execution!");
                return false;
            }
        } else {
            invocation.run();
            return true;
        }
    }

    protected Invocation getRunnable(FrameBuffer frameBuffer) {
        return new Invocation(frameBuffer);
    }
}
