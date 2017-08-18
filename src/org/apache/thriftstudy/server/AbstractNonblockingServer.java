package org.apache.thriftstudy.server;

import org.apache.thriftstudy.transport.TNonblockingTransport;
import org.apache.thriftstudy.transport.TServerTransport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 提供Nonblocking TServer共同的方法和类的实现
 *
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/16
 */
public abstract class AbstractNonblockingServer extends TServer {

    final long MAX_READ_BUFFER_BYTES;

    final AtomicLong readBufferBytesAllocated = new AtomicLong(0);

    public static abstract class AbstractNonblockingServerArgs<T extends AbstractNonblockingServerArgs<T>> extends AbstractServerArgs<T> {
        public long maxReadBufferBytes = Long.MAX_VALUE;

        public AbstractNonblockingServerArgs(TServerTransport transport) {
            super(transport);

        }
    }

    public AbstractNonblockingServer(AbstractNonblockingServerArgs args) {
        super(args);
        MAX_READ_BUFFER_BYTES = args.maxReadBufferBytes;
    }

    protected abstract class AbstractSelectorThread extends Thread {

        protected final Selector selector;

        public AbstractSelectorThread() throws IOException {
            selector = SelectorProvider.provider().openSelector();
        }

        protected void handleRead(SelectionKey selectionKey) {
            FrameBuffer buffer = (FrameBuffer) selectionKey.attachment();
            if (!buffer.read()) {
                cleanupSelectionKey(selectionKey);
                return;
            }

            if (buffer.isFrameFullyRead()) {
                if (!requestInvoke(buffer)) {
                    cleanupSelectionKey(selectionKey);
                }
            }
        }

        protected void handleWrite(SelectionKey selectionKey) {
            FrameBuffer buffer = (FrameBuffer) selectionKey.attachment();
            if (!buffer.write()) {
                cleanupSelectionKey(selectionKey);
            }
        }

        protected void cleanupSelectionKey(SelectionKey selectionKey) {
            FrameBuffer buffer = (FrameBuffer) selectionKey.attachment();
            if (buffer != null) {
                buffer.close();
            }
            selectionKey.cancel();
        }
    }

    /**
     * Possible states for the FrameBuffer state machine.
     */
    private enum FrameBufferState {
        // in the midst of reading the frame size off the wire
        READING_FRAME_SIZE,
        // reading the actual frame data now, but not all the way done yet
        READING_FRAME,
        // completely read the frame, so an invocation can now happen
        READ_FRAME_COMPLETE,
        // waiting to get switched to listening for write events
        AWAITING_REGISTER_WRITE,
        // started writing response data, not fully complete yet
        WRITING,
        // another thread wants this framebuffer to go back to reading
        AWAITING_REGISTER_READ,
        // we want our transport and selection key invalidated in the selector
        // thread
        AWAITING_CLOSE
    }

    public class FrameBuffer {
        protected final TNonblockingTransport trans_;

        protected final SelectionKey selectionKey_;

        protected final AbstractSelectorThread selectorThread_;

        protected ByteBuffer buffer_;

        protected FrameBufferState state_ = FrameBufferState.READING_FRAME_SIZE;

        public FrameBuffer(TNonblockingTransport transport, SelectionKey selectionKey, AbstractSelectorThread selectorThread) {
            trans_ = transport;
            selectionKey_ = selectionKey;
            selectorThread_ = selectorThread;
            buffer_ = ByteBuffer.allocate(4);
        }

        public boolean read() {
            if (state_ == FrameBufferState.READING_FRAME_SIZE) {
                if (!internalRead()) {
                    return false;
                }
                if (buffer_.remaining() == 0) {
                    int frameSize = buffer_.getInt(0);
                    if (frameSize < 0) {
                        System.out.println("Read an invalid frame size of " + frameSize
                                + ". Are you using TFramedTransport on the client side?");
                        return false;
                    }
                    if (frameSize > MAX_READ_BUFFER_BYTES) {
                        System.out.println("Read a frame size of " + frameSize
                                + ", which is bigger than the maximum allowable buffer size for ALL connections.");
                        return false;
                    }
                    if (readBufferBytesAllocated.get() + frameSize > MAX_READ_BUFFER_BYTES) {
                        //超过最大读取字节数限制，等待下次轮询在尝试读取
                        return true;
                    }
                    buffer_ = ByteBuffer.allocate(frameSize + 4);
                    buffer_.putInt(frameSize);
                    state_ = FrameBufferState.READING_FRAME;
                } else {
                    return true;
                }
            }

            if (state_ == FrameBufferState.READING_FRAME) {
                if (!internalRead()) {
                    return false;
                }
                if (buffer_.remaining() == 0) {
                    selectionKey_.interestOps(0);
                    state_ = FrameBufferState.READ_FRAME_COMPLETE;
                    return true;
                }
            }
            return false;
        }

        public boolean write() {
            if (state_ == FrameBufferState.WRITING) {
                try {
                    if (trans_.write(buffer_) < 0) {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                if (buffer_.remaining() == 0) {
                    prepareRead();
                }
                return true;
            }
            System.out.println("Write was called, but state is invalid (" + state_ + ")");
            return false;
        }

        public boolean isFrameFullyRead() {
            return state_ == FrameBufferState.READ_FRAME_COMPLETE;
        }

        public void close() {
        }

        private boolean internalRead() {
            try {
                if (trans_.read(buffer_) < 0) {
                    return false;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void prepareRead() {
            selectionKey_.interestOps(SelectionKey.OP_READ);
            buffer_ = ByteBuffer.allocate(4);
            state_ = FrameBufferState.READING_FRAME_SIZE;
        }
    }

    /**
     * 启动服务启动时需要启动的所有线程
     *
     * @return true如果所有线程都成功启动，false如果线程启动失败
     */
    protected abstract boolean startThreads();

    protected abstract boolean requestInvoke(FrameBuffer frameBuffer);

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
