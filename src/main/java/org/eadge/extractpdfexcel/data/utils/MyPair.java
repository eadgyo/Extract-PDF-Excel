package org.eadge.extractpdfexcel.data.utils;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Created by eadgyo on 19/07/16.
 *
 */
public class MyPair<L, R> extends Pair
{
    private L l;
    private R r;

    public MyPair(L l, R r)
    {
        this.l = l;
        this.r = r;
    }

    @Override
    public L getLeft()
    {
        return l;
    }

    public void setLeft(L l)
    {
        this.l = l;
    }

    @Override
    public R getRight()
    {
        return r;
    }

    public void setRight(R r)
    {
        this.r = r;
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public Object setValue(Object o)
    {
        return null;
    }
}
