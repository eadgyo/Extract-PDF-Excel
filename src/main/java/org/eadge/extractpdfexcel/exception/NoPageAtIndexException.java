package org.eadge.extractpdfexcel.exception;

/**
 * Created by eadgyo on 16/07/16.
 *
 */
public class NoPageAtIndexException extends Exception
{
    private int page;

    public NoPageAtIndexException(int page)
    {
        this.page = page;
    }

    @Override
    public String getMessage()
    {
        return "No page at index: " + page;
    }
}
