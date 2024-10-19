/*
 * Decompiled with CFR 0.152.
 */
package kcp.highway.erasure.fec;

import java.util.ArrayList;

public class MyArrayList<E>
extends ArrayList<E> {
    public MyArrayList() {
    }

    public MyArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
    }
}

