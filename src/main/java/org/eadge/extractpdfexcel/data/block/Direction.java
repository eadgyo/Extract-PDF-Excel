package org.eadge.extractpdfexcel.data.block;

/**
 * Created by eadgyo on 16/07/16.
 * <p/>
 * Used to indicate direction.
 */
public enum Direction
{
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    public boolean isInPortrayMode()
    {
        return this.equals(LEFT) || this.equals(RIGHT);
    }

    public int getLaneDirection()
    {
        return isInPortrayMode() ? 0 : 1;
    }

    public int getOppositeLaneDirection()
    {
        return isInPortrayMode() ? 1 : 0;
    }
}
