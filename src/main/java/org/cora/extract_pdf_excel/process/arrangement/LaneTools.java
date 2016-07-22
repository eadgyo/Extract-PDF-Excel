package org.cora.extract_pdf_excel.process.arrangement;

import org.cora.extract_pdf_excel.data.geom.Rectangle2;
import org.cora.extract_pdf_excel.data.utils.MyPair;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.block.Direction;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;

import java.util.Map;
import java.util.Set;

/**
 * Created by eadgyo on 20/07/16.
 *
 * Tools to insert or get lane
 */
class LaneTools
{
    /**
     * Get lowerLane from block.
     * Lane are sorted along their opposite axis.
     *
     * @param oppositeAxis opposite axis of lane
     * @param block        used to getPos lower lane
     * @param lanes        group of sorted lanes
     *
     * @return lowerLane or null if there is no lower lane
     */
    static MyPair<Lane, Lane> getLowerAndHigherLane(int oppositeAxis, Block block, Lanes lanes)
    {
        // Take higher coordinate of lane to getPos
        Double key = block.getBound().getPos(oppositeAxis);

        // Take higher lane which is not colliding
        Map.Entry<Double, Lane> higherLaneEntry = lanes.getHigherLaneEntry(key);

        // If higher lane exists
        if (higherLaneEntry != null)
        {
            // Then take lower lane that may be colliding with block from higher lane
            return new MyPair<Lane, Lane>(lanes.getLowerLane(higherLaneEntry.getKey()), higherLaneEntry.getValue());
        }
        else
        {
            // Higher lane doesn't exist, there is maybe one lower lane colliding
            return new MyPair<Lane, Lane>(lanes.getLowerLane(key), null);
        }
    }

    /**
     * Get position of rect1 relative to rect2
     *
     * @param oneAxis used axis
     * @param rect1   first rectangle
     * @param rect2   second rectangle
     *
     * @return positon of rect1 relative to rect2, if they have the same lower position, rect1 is considered at the
     * left of rect2
     */
    static Direction getRelativeDirection(int oneAxis, Rectangle2 rect1, Rectangle2 rect2)
    {
        // If rect1 is before rect2 on one
        if (rect1.getPos(oneAxis) <= rect2.getPos(oneAxis))
        {
            // rect1 is before rect2
            return Direction.LEFT;
        }
        else
        {
            // rect1 is after rect2
            return Direction.RIGHT;
        }
    }

    static void insertBlocksInRightLane(int axis,
                                        int oppositeAxis,
                                        Set<Block> blocks,
                                        Lane lowerLane,
                                        Lane higherLane)
    {
        for (Block block : blocks)
        {
            // If block need to be inserted in lowerLane
            if (block.getPos(oppositeAxis) < higherLane.getPos(oppositeAxis))
            {
                // Block need to be inserted in LowerLane
                lowerLane.addBlockAndFitLane(axis, oppositeAxis, block);
            }
            else
            {
                // Block need to be inserted in HigherLane
                higherLane.addBlockAndFitLane(axis, oppositeAxis, block);
            }
        }
    }
}
