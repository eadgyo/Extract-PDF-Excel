package org.cora.extract_pdf_excel.process.arrangement;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;

import java.util.Map;

/**
 * Created by eadgyo on 17/07/16.
 */
public class BlockSorter
{
    /**
     * Insert block in sorted lanes
     *
     * @param axis         axis lane
     * @param oppositeAxis opposite axis lane
     * @param block        inserted block
     * @param lanes        sorted lanes
     */
    public static void insertInLanes(int axis, int oppositeAxis, Block block, Lanes lanes)
    {
        // Get lowerLane from block
        Lane lowerLane = getLowerLane(oppositeAxis, block, lanes);

        // If lowerLane exists AND lowerLane is colliding
            if (lowerLane != null && isCollidingWithBlock(oppositeAxis, block, lowerLane))
            {
                // Compare block to existing in lane blocks


            }

            // While there are block colliding along opposite axis
                // Take the inserted block or existing block, with higher coordinate
                // If another lane is colliding with higherBlock
                    // Save this lane to reinsert blocks
                    // Change position of this lane as the min of actualLane coordinate and higherBlock coordinate
                // Else
                    // Create a new lane and insert it in lanes
                    // Save lane to reinsert blocks

            // Save lower lane blocks
            // Clean lower lane
            // Clear width by setting lower lane length along axis to 0
            // Set lower lane length along opposite axis to min of actual length and difference of low coordinate
            // of savedLane and low coordinate of lowerLane to remove savedLane and lowerLane collision.

            // Reinsert all savedBlocks of lowerLane in his corresponding lane (savedLane or lowerLane)


        // Else the correct lane does no exist
            // Create a new lane with block rect
            // Insert block in created lane

    }

    /**
     * Get lowerLane from block.
     * Lane are sorted along their opposite axis.
     *
     * @param oppositeAxis opposite axis of lane
     * @param block used to getPos lower lane
     * @param lanes group of sorted lanes
     *
     * @return lowerLane or null if there is no lower lane
     */
    private static Lane getLowerLane(int oppositeAxis, Block block, Lanes lanes)
    {
        // Take higher coordinate of lane to getPos
        Double key = block.getBound().getPos(oppositeAxis);

        // Take higher lane which is not colliding
        Map.Entry<Double, Lane> higherLaneEntry = lanes.getHigherLaneEntry(key);

        // If higher lane exists
        if (higherLaneEntry != null)
        {
            // Then take lower lane that may be colliding with block from higher lane
            return lanes.getLowerLane(higherLaneEntry.getKey());
        }
        else
        {
            // Higher lane doesn't exist, there is maybe one lower lane colliding
            return lanes.getLowerLane(key);
        }
    }

    /**
     * Test if a block is colliding with a lane
     *
     * @param oppositeAxis opposite axis of lane
     * @param block tested block
     * @param lane tested lane
     * @return true if they are colliding, false if they aren't
     */
    private static boolean isCollidingWithBlock(int oppositeAxis, Block block, Lane lane)
    {
        return block.getPos(oppositeAxis) < lane.getPos(oppositeAxis) + lane.getLength(oppositeAxis) &&
                block.getPos(oppositeAxis) + block.getLength(oppositeAxis) > lane.getPos(oppositeAxis);
    }

    /**
     * Get in lane block colliding with block
     * @param oppositeAxis opposite lane axis
     * @param block checked block
     * @return block that is colliding with checked block, or null if there are no block.
     */
    private static Block getInLaneBlockColliding(int oppositeAxis, Block block, Lane lane)
    {
        // Get lower block
        return null;
    }

    /**
     * Add a block in lane and fit lane with block and with existing lanes
     *
     * @param axis          lane axis
     * @param oppositeAxis  opposite lane axis
     * @param block added block
     * @param lane inserting lane
     */
    private static void addBlockAndFitLane(int axis, int oppositeAxis, Block block, Lane lane, Lanes lanes)
    {
        // Save key to check if the lower bound has changed.
        Double savedKey = lane.getPos(oppositeAxis);

        // Add block, lower bound may changed
        lane.addBlockAndFitLane(axis, oppositeAxis, block);

        // If lower bound has changed
        if (savedKey != lane.getPos(oppositeAxis))
        {
            Lane savedLane = lanes.removeLane(savedKey);

            assert (lane == savedLane);

            lanes.insertLane(oppositeAxis, lane);
        }
    }
}
