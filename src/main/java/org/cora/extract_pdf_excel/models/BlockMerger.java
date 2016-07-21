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
     * Analyse and merge two by two, blocks that are respecting merge conditions.
     *
     * @param blocks collections of blocks tested to be merged.
     */
    public abstract void mergeIfNecessaryBlocks(Collection<Block> blocks);
}
