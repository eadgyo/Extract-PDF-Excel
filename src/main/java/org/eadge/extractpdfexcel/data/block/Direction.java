package org.eadge.extractpdfexcel.data.block;

/**
 * Created by eadgyo on 16/07/16.
 * <p/>
 * Used to indicate direction.
 */
public enum Direction
{
    LEFT,
    RIGHT,
    TOP,
    BOTTOM;

    public boolean isInPortrayMode()
    {
        return this.equals(TOP) || this.equals(BOTTOM);
    }

    public int getLaneDirection()
    {
        return isInPortrayMode() ? 0 : 1;
    }

    public int getOppositeLaneDirection()
    {
        return isInPortrayMode() ? 1 : 0;
    }

    public int getLaneDirectionVector()
    {
        return isInPortrayMode() ? 1 : 0;
    }

    public int getOppositeLaneDirectionVector()
    {
        return isInPortrayMode() ? 0 : 1;
    }
}
