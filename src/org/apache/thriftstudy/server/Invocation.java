package org.apache.thriftstudy.server;

import org.apache.thriftstudy.server.AbstractNonblockingServer.FrameBuffer;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/25
 */
class Invocation implements Runnable {

    private final FrameBuffer frameBuffer;

    public Invocation(final FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    @Override
    public void run() {
        frameBuffer.invoke();
    }
}
