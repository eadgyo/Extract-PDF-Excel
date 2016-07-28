package org.cora.extract_pdf_excel.data.lane;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;

import java.util.*;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Lane containing sorted sorted blocks.
 */
public class Lane
{
    private Rectangle2             rectangle;
    private TreeMap<Double, Block> blocks;

    public Lane()
    {
        rectangle = new Rectangle2();
        blocks = new TreeMap<Double, Block>();
    }

    public void setRectangle(Rectangle2 rectangle)
    {
        this.rectangle.set(rectangle);
    }

    public double getPos(int axis) { return this.rectangle.getPos(axis); }

    /**
     * Get the coordinate of the end of the lane along one axis.
     * @param axis used axis
     * @return end coordinate of the lane along one axis.
     */
    public double getEndPos(int axis) { return this.rectangle.getEndPos(axis); }

    public double getLength(int axis) { return this.rectangle.getLength(axis); }

    public void setPos(int axis, double value)
    {
        this.rectangle.setPos(axis, value);
    }

    public void setLength(int axis, double value)
    {
        this.rectangle.setLength(axis, value);
    }

    public TreeMap<Double, Block> getBlocks()
    {
        return blocks;
    }

    public Collection<Block> getBlocksCollection()
    {
        return blocks.values();
    }

    public void setBlocks(TreeMap<Double, Block> blocks)
    {
        this.blocks = blocks;
    }

    public Set<Block> copyBlocks() { return new HashSet<>(blocks.values()); }

    public void clearBlocks() { blocks.clear(); }

    /**
     * Add block to the lane and fit bound lane to added block
     *
     * @param axis         lane axis
     * @param oppositeAxis opposite axis of the lane
     * @param block        added block
     */
    public void addBlockAndFitLane(int axis, int oppositeAxis, Block block)
    {
        addBlock(axis, block);
        fitLane(axis, oppositeAxis, block);
    }

    /**
     * Add block to lane without updating lane bound
     *
     * @param axis lane axis
     * @param block added block
     */
    public void addBlock(int axis, Block block)
    {
        blocks.put(block.getPos(axis), block);
    }

    /**
     * Fit lane to added rectangle
     *
     * @param axis         lane axis
     * @param oppositeAxis lane opposite axis
     * @param block        added block
     */
    public void fitLane(int axis, int oppositeAxis, Block block)
    {
        // On main axis
        fitLaneOneAxis(axis, block);

        // On opposite axis
        fitLaneOneAxis(oppositeAxis, block);
    }

    /**
     * Fit one axis lane to added block
     *
     * @param axis  lane axis
     * @param block added blocks
     */
    private void fitLaneOneAxis(int axis, Block block)
    {
        // If block lower coordinate is before lane lower coordinate
        if (block.getPos(axis) < rectangle.getPos(axis))
        {
            // Change lane lower coordinate to block lower coordinate
            rectangle.addLength(axis, rectangle.getPos(axis) - block.getPos(axis));
            rectangle.setPos(axis, block.getPos(axis));
        }

        // If block higher coordinate is after lane higher coordinate
        if (block.getPos(axis) + block.getLength(axis) > rectangle.getPos(axis) + rectangle.getLength(axis))
        {
            // Change lane higher coordinate to lane higher coordinate
            rectangle.setLength(axis, block.getPos(axis) + block.getLength(axis) - rectangle.getPos(axis));
        }
    }

    /**
     * @return number of lanes
     */
    public int size()
    {
        return blocks.size();
    }

    /**
     * Get a block from a key
     *
     * @param key block linked key
     *
     * @return block if it exists or null if key is not linked
     */
    public Block getBlock(double key)
    {
        return blocks.get(key);
    }

    /**
     * Get block lower than key
     *
     * @param key to be checked
     * @return lower block and his value in map
     */
    public Map.Entry<Double, Block> getLowerBlockEntry(double key)
    {
        return blocks.lowerEntry(key);
    }

    /**
     * Get block lower than key
     *
     * @param key to be checked
     * @return lower block
     */
    public Block getLowerBlock(double key)
    {
        Map.Entry<Double, Block> lowerBlockEntry = getLowerBlockEntry(key);

        return (lowerBlockEntry != null) ? lowerBlockEntry.getValue() : null;
    }

    /**
     * Get block higher than key
     *
     * @param key to be checked
     * @return higher block and his value in map
     */
    public Map.Entry<Double, Block> getHigherBlockEntry(double key)
    {
        return blocks.higherEntry(key);
    }

    /**
     * Get block higher than key
     *
     * @param key to be checked
     * @return higher block
     */
    public Block getHigherBlock(double key)
    {
        Map.Entry<Double, Block> higherBlockEntry = getHigherBlockEntry(key);

        return (higherBlockEntry != null) ? higherBlockEntry.getValue() : null;
    }

    public Rectangle2 getBound()
    {
        return rectangle;
    }

    public void remove(int axis, Block block)
    {
        Block removed = blocks.remove(block.getPos(axis));

        assert (removed != null);
    }

    public Block remove(Double key)
    {
        return blocks.remove(key);
    }

    /**
     * Reset lane bound and fit lane to in lanes' block. If there are no blocks, set the length to 0
     *
     * @param axis         lane axis
     * @param oppositeAxis opposite axis of the lane
     */
    public void resetAndFitLaneToInBlocks(int axis, int oppositeAxis)
    {
        // If there are no blocks in lane
        if (blocks.size() == 0)
        {
            setLength(axis, 0);
            setLength(oppositeAxis, 0);
            return;
        }

        Collection<Block> values = blocks.values();

        // Start to reset lane size with first block
        Iterator<Block> iterator = values.iterator();
        Block first = iterator.next();
        this.rectangle.set(first.getBound());

        // Reinsert all blocks
        while (iterator.hasNext())
        {
            Block reinsertedBlock = iterator.next();
            fitLaneOneAxis(axis, reinsertedBlock);
            fitLaneOneAxis(oppositeAxis, reinsertedBlock);
        }
    }

    /**
     * Fit lane to an higher lane. Only end bound of lane is changed.
     *
     * @param oppositeAxis opposite lane axis
     * @param higherLane lane higher than actual lane
     */
    public void fitToHigherLane(int oppositeAxis, Lane higherLane)
    {
        double maxLength = higherLane.getPos(oppositeAxis) - getPos(oppositeAxis);

        setLength(oppositeAxis, Math.min(getLength(oppositeAxis), maxLength));
    }

    /**
     * Check if lane contain block
     *
     * @param axis lane axis
     * @param block checked block
     *
     * @return true if block is in lane, false if it isn't
     */
    public boolean containsBlock(int axis, Block block)
    {
        Block gettedBlock = getBlock(block.getPos(axis));

        return block == gettedBlock;
    }

    /**
     * Get all blocks in set
     * @return list of blocks, not keeping data sorted
     */
    public Collection<Block> retrieveBlocks()
    {
        return blocks.values();
    }
}
