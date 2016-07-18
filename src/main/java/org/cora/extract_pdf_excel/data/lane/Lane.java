package org.cora.extract_pdf_excel.data.lane;

import com.itextpdf.awt.geom.Rectangle;
import org.cora.extract_pdf_excel.data.block.Block;

import java.util.TreeMap;

/**
 * Created by eadgyo on 12/07/16.
 */
public class Lane
{
    private Rect                   rectangle;
    private TreeMap<Double, Block> blocks;

    public Lane()
    {
        rectangle = new Rect();
        blocks = new TreeMap<Double, Block>();
    }

    public Rectangle getRectangle()
    {
        return rectangle;
    }

    public void setRectangle(Rect rectangle)
    {
        this.rectangle = rectangle;
    }

    public double getPos(int i) { return this.rectangle.getPos(i); }

    public double getLength(int i) { return this.rectangle.getLength(i); }

    public void setPos(int i, double value)
    {
        this.rectangle.setPos(i, value);
    }

    public void setLength(int i, double value)
    {
        this.rectangle.setLength(i, value);
    }

    public TreeMap<Double, Block> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(TreeMap<Double, Block> blocks)
    {
        this.blocks = blocks;
    }

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

    private void addBlock(int axis, Block block)
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
    private void fitLane(int axis, int oppositeAxis, Block block)
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
}
