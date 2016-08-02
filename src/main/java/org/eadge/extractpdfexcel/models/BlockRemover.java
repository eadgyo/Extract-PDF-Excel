package org.eadge.extractpdfexcel.models;

import org.eadge.extractpdfexcel.data.block.Block;

import java.util.Collection;

/**
 * Created by eadgyo on 21/07/16.
 *
 * Remove block under some conditions.
 */
public abstract class BlockRemover
{
    /**
     * Remove block under some conditions.
     * @param blocks list of blocks to treat.
     */
    public abstract void removeBlock(Collection<Block> blocks);
}
