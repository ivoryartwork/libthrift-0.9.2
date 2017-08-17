package org.apache.thriftstudy.transport;

import java.nio.channels.Selector;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public abstract class TNonblockingServerTransport extends TServerTransport {

    public abstract void registerSelector(Selector selector);
}
