package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public final class TList {

    public TList() {
        this(TType.STOP, 0);
    }

    public TList(byte e, int s) {
        elemType = e;
        size = s;
    }

    public final byte elemType;
    public final int size;
}
