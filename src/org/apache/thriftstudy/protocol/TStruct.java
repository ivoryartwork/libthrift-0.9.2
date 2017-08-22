package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public final class TStruct {

    public TStruct() {
        this("");
    }

    public TStruct(String n) {
        name = n;
    }

    public final String name;
}
