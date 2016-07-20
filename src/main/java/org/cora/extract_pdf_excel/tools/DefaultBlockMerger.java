package org.cora.extract_pdf_excel.tools;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.models.BlockMerger;

import java.util.Collection;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Merge blocks if they are too close, have at the least one same fontColor and one same backColor.
 */
public class DefaultBlockMerger extends BlockMerger
{
    @Override
    public boolean needBlockMerging(Block a, Block b)
    {
        return false;
    }

    @Override
    public void mergeIfNecessaryBlocks(Collection<Block> blocks)
    {

    }
}
