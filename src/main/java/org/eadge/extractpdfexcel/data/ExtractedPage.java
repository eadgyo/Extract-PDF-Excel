package org.eadge.extractpdfexcel.data;

import org.eadge.extractpdfexcel.data.block.Block;

import java.util.*;

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

    public void cleanDuplicatedBlocks()
    {
        Map<String, ArrayList<Block>> blocksMap = new HashMap<>();

        // Start adding each block using contained text as key
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            // If the block is not an empty block
            if (block.getOriginalText().equals(""))
            {
                iterator.remove();
            }
            else
            {
                String           key    = block.getOriginalText();
                ArrayList<Block> blocks = blocksMap.get(key);

                if (blocks == null)
                {
                    blocks = new ArrayList<>();
                    blocksMap.put(key, blocks);
                }

                blocks.add(block);
            }
        }

        // Compare and remove duplicated blocks
        for (Iterator<Block> iterator = blocks.iterator(); iterator.hasNext(); )
        {
            Block block = iterator.next();

            String key = block.getOriginalText();
            ArrayList<Block> blocks = blocksMap.get(key);

            // Try to find a duplicated block with the same key and same position
            for (Block comparedBlock : blocks)
            {
                if (comparedBlock != block &&
                        block.getPos(0) == comparedBlock.getPos(0) &&
                        block.getPos(1) == comparedBlock.getPos(1))
                {
                    // The block is duplicated
                    // Remove it from the map and the source collection
                    blocks.remove(block);
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
