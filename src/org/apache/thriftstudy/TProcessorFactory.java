package org.apache.thriftstudy;

import org.apache.thriftstudy.transport.TTransport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public class TProcessorFactory {

    private final TProcessor processor_;

    public TProcessorFactory(TProcessor processor) {
        processor_ = processor;
    }

    public TProcessor getProcessor(TTransport trans) {
        return processor_;
    }
}
