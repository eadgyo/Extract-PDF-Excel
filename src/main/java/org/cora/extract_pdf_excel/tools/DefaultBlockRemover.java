package org.cora.extract_pdf_excel.tools;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Rect;
import org.cora.extract_pdf_excel.models.BlockRemover;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by eadgyo on 19/07/16.
 * <p/>
 * Remove block if it already exists
 */
public class DefaultBlockRemover extends BlockRemover
{
    /**
     * Bounds of the extracted pdf
     */
    private Rect bounds;

    /**
     * @param bounds bound delimiting valid blocks.
     */
    public DefaultBlockRemover(Rect bounds)
    {
        this.bounds = bounds;
    }

    @Override
    public void removeBlock(Collection<Block> blocks)
    {
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            if (!areRectColliding(bounds, block.getBound()))
            {
                iterator.remove();
            }
        }
    }

    /**
     * Check if two rectangles are colliding
     * @param rect1 first rectangle checked
     * @param rect2 second rectangle checked
     * @return true if they are colliding, false if they are not colliding
     */
    private static boolean areRectColliding(Rect rect1, Rect rect2)
    {
        return areCollidingOnAxis(0, rect1, rect2) && areCollidingOnAxis(1, rect1, rect2);
    }

    /**
     * Check if two rectangles are colliding along on axis
     * @param axis checked axis
     * @param rect1 first rectangle checked
     * @param rect2 second rectangle checked
     * @return true if they are colliding along axis, false if they are not colliding along axis
     */
    private static boolean areCollidingOnAxis(int axis, Rect rect1, Rect rect2)
    {
        return rect1.getPos(axis) < rect2.getPos(axis) + rect2.getLength(axis) &&
                rect1.getPos(axis) + rect1.getLength(axis) > rect2.getPos(axis);
    }
}
