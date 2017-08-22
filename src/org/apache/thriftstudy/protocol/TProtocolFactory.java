package org.apache.thriftstudy.protocol;

import org.apache.thriftstudy.transport.TTransport;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/17
 */
public interface TProtocolFactory {

    public TProtocol getProtocol(TTransport trans);
}
