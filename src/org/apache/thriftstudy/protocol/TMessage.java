package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public final class TMessage {

    public final String name;
    public final byte type;
    public final int seqid;

    public TMessage() {
        this("", TType.STOP, 0);
    }

    public TMessage(String n, byte t, int s) {
        name = n;
        type = t;
        seqid = s;
    }

    @Override
    public String toString() {
        return "<TMessage name:'" + name + "' type:" + type + " seqid:" + seqid + '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TMessage tMessage = (TMessage) o;

        if (type != tMessage.type) return false;
        if (seqid != tMessage.seqid) return false;
        return name != null ? name.equals(tMessage.name) : tMessage.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) type;
        result = 31 * result + seqid;
        return result;
    }
}
