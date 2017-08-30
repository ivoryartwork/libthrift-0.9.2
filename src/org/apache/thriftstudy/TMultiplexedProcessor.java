package org.apache.thriftstudy;

import org.apache.thriftstudy.protocol.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/30
 */
public class TMultiplexedProcessor implements TProcessor {

    private final Map<String, TProcessor> SERVICE_PROCESSOR_MAP = new HashMap<String, TProcessor>();

    public void registerProcessor(String serviceName, TProcessor processor) {
        SERVICE_PROCESSOR_MAP.put(serviceName, processor);
    }

    @Override
    public boolean process(TProtocol iprot, TProtocol oprot) throws TException {
        TMessage message = iprot.readMessageBegin();
        if (message.type != TMessageType.CALL && message.type != TMessageType.ONEWAY) {
            throw new TException("This should not have happended!?");
        }

        int index = message.name.indexOf(TMultiplexedProtocol.SEPARATOR);
        if (index < 0) {
            throw new TException("Service name not found in message name: " + message.name + ".  Did you " +
                    "forget to use a TMultiplexProtocol in your client?");
        }
        String serviceName = message.name.substring(0, index);
        TProcessor actualProcessor = SERVICE_PROCESSOR_MAP.get(serviceName);
        if (actualProcessor == null) {
            throw new TException("Service name not found: " + serviceName + ".  Did you forget " +
                    "to call registerProcessor()?");
        }
        TMessage tMessage = new TMessage(message.name.substring(serviceName.length() + TMultiplexedProtocol.SEPARATOR.length()),
                message.type, message.seqid);

        actualProcessor.process(new StoredMessageProtocol(iprot, tMessage), oprot);
        return false;
    }

    private class StoredMessageProtocol extends TProtocolDecorator {

        private TMessage messageBegin;

        public StoredMessageProtocol(TProtocol protocol, TMessage messageBegin) {
            super(protocol);
            this.messageBegin = messageBegin;
        }

        @Override
        public TMessage readMessageBegin() throws TException {
            return messageBegin;
        }
    }
}
