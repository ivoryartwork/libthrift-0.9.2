package org.apache.thriftstudy.protocol;

/**
 * @author Yaochao
 * @version 1.0
 * @date 2017/8/22
 */
public final class TField {

    public final String name;
    public final byte type;
    public final short id;

    public TField() {
        this("", TType.STOP, (short) 0);
    }

    public TField(String n, byte t, short i) {
        name = n;
        type = t;
        id = i;
    }

    @Override
    public String toString() {
        return "<TField name:'" + name + "' type:" + type + " id=" + id + '>';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TField tField = (TField) o;

        if (type != tField.type) return false;
        if (id != tField.id) return false;
        return name != null ? name.equals(tField.name) : tField.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (int) type;
        result = 31 * result + (int) id;
        return result;
    }
}
