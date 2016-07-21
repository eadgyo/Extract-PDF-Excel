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

    private static boolean areRectColliding(Rect rect1, Rect rect2)
    {
        return rect1.getPos(0) < rect2.getPos(0) + rect2.getLength(0) &&
                rect1.getPos(0) + rect1.getLength(0) > rect2.getPos(0) &&
                rect1.getPos(1) < rect2.getPos(1) + rect2.getLength(1) &&
                rect1.getPos(1) + rect1.getLength(1) > rect2.getPos(1);
    }
}
