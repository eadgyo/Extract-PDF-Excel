package org.eadge.extractpdfexcel.debug.tests;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Result transform
 */
class Result
{
    static String transformResult(boolean result)
    {
        if (result)
            return "[ Ok ]";
        else
            return "[ Failed ]";
    }
}
