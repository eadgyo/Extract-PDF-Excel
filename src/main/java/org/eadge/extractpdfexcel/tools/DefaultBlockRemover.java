package org.eadge.extractpdfexcel.tools;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.models.BlockRemover;

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
    private Rectangle2 bounds;

    /**
     * @param bounds bound delimiting valid blocks.
     */
    public DefaultBlockRemover(Rectangle2 bounds)
    {
        this.bounds = bounds;
    }

    @Override
    public void removeBlock(Collection<Block> blocks)
    {
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            if (!bounds.areColliding(block.getBound()))
            {
                iterator.remove();
            }
        }
    }
}
