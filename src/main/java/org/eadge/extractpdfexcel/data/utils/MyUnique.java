package org.eadge.extractpdfexcel.data.utils;

/**
 * Created by eadgyo on 21/07/16.
 *
 */
public class MyUnique<T>
{
    private T t;

    public MyUnique()
    {
        this.t = null;
    }

    public MyUnique(T t)
    {
        this.t = t;
    }

    public T getT()
    {
        return t;
    }

    public void setT(T t)
    {
        this.t = t;
    }
}
