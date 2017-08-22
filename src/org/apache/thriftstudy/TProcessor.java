package org.apache.thriftstudy;

import org.apache.thriftstudy.protocol.TProtocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/21
 */
public interface TProcessor {
    
    public boolean process(TProtocol in, TProtocol out) throws TException;
}
