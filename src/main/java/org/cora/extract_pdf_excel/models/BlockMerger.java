package org.cora.extract_pdf_excel.models;



import org.cora.extract_pdf_excel.data.block.Block;

import java.util.Collection;

/**
 * Created by eadgyo on 14/07/16.
 * <p/>
 * Merge blocks if they respect merge conditions.
 */
public abstract class BlockMerger
{
    /**
     * Determine if two blocks respect merge conditions.
     *
     * @param a first block.
     * @param b second block.
     * @return <tt> true </tt> if two blocks respect merge conditions.
     */
    protected abstract boolean needBlockMerging(Block a, Block b);

    /**
     * Merge two blocks, merge their text and rectangle.
     *
     * @param axis used axis to merge in the right order
     * @param removedBlock Block that will be removed, and contains first part.
     * @param mergeBlock Block that will contain two blocks, and contains at input the second part.
     *
     */
    protected abstract void mergeBlock(int axis, Block removedBlock, Block mergeBlock);

    /**
     * Analyse and merge two by two, blocks that are respecting merge conditions.
     *
     * @param blocks collections of blocks tested to be merged.
     */
    public abstract void mergeIfNecessaryBlocks(Collection<Block> blocks);
}
