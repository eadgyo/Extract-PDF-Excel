package org.cora.extract_pdf_excel.process.arrangement;

import org.cora.extract_pdf_excel.data.SortedData;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;

import java.util.Map;

/**
 * Created by eadgyo on 17/07/16.
 */
public class BlockSorter
{
    public static void insertInSortedData(Block block, SortedData sortedData)
    {

    }

    public static void insertInLanes(Block block, Lanes lanes)
    {
        // Get lowerLane from block
        //getLowerLane()

        // If lowerLane exists AND lowerLane is colliding
            // Compare block to existing in lane blocks
            // If there are one block colliding along opposite axis
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
     * Get lowerLane from block
     *
     * @param block used to getPos lower lane
     * @param axisIndex axis of lane, 0 for Line and 1 for Column
     * @param oppositeIndex opposite axis of lane, 1 for Line and 0 for Column
     *
     * @return lowerLane or null if there is no lower lane
     */
    private static Lane getLowerLane(Lanes lanes, Block block, int axisIndex, int oppositeIndex)
    {
        // Take higher coordinate of lane to getPos
        Double key = block.getBound().getPos(axisIndex);

        // Take higher lane which is not colliding
        Map.Entry<Double, Lane> blockEntry = lanes.getHigherLaneEntry(key);

        if (blockEntry != null)
        {
            // Then take lower lane that may be colliding with block
            return lanes.getLowerLane(blockEntry.getKey());
        }
        else
        {
            return lanes.getLowerLane(key);
        }
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
