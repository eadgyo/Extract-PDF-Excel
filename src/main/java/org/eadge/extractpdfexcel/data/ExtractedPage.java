package org.eadge.extractpdfexcel.data;

import org.eadge.extractpdfexcel.data.block.Block;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by eadgyo on 16/07/16.
 * <p/>
 * Holds blocks in page and pdf length
 */
public class ExtractedPage
{
    /**
     * Length of page
     */
    private float width;
    private float height;

    /**
     * Data in page
     */
    private Collection<Block> blocks;

    public ExtractedPage(float width, float height, Collection<Block> blocks)
    {
        this.width = width;
        this.height = height;

        this.blocks = blocks;
    }

    public ExtractedPage(float width, float height)
    {
        this.width = width;
        this.height = height;

        this.blocks = new ArrayList<Block>();
    }

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public Collection<Block> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }

    public int numberOfBlocks()
    {
        return blocks.size();
    }

    /**
     * Add block in page
     *
     * @param block added block
     */
    public void addBlock(Block block)
    {
        blocks.add(block);
    }

    public void addAllBlocks(Collection<Block> blocks)
    {
        this.blocks.addAll(blocks);
    }
}
