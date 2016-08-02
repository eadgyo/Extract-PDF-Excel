package org.eadge.extractpdfexcel.models;

import org.eadge.extractpdfexcel.data.block.Block;

import java.util.Collection;

/**
 * Created by eadgyo on 14/07/16.
 * <p/>
 * Format text.
 */
public abstract class StringFormatter
{
    /**
     * Format collection of blocks containing unformatted text. Update their bounds and formatted Text.
     *
     * @param rawBlocks collection of blocks containing unformatted text.
     */
    public void formatBlocks(Collection<Block> rawBlocks)
    {
        for (Block rawBlock : rawBlocks)
        {
            formatBlock(rawBlock);
        }
    }

    /**
     * Format one block containing unformatted text. Update bounds and formatted Text.
     *
     * @param rawBlock block containing unformatted text.
     */
    public abstract void  formatBlock(Block rawBlock);
}
