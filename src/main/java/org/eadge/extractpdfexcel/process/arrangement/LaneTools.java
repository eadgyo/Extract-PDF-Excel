package org.eadge.extractpdfexcel.process.arrangement;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.data.lane.Lanes;
import org.eadge.extractpdfexcel.data.utils.MyPair;

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
     * Get first colliding lane from block, and higher lane if exists.
     * Lane are sorted along their opposite axis.
     *
     * @param oppositeAxis opposite axis of lane
     * @param block        used to getPos lower lane
     * @param lanes        group of sorted lanes
     *
     * @return couple of first colliding lane and his higher lane. If there is no first colliding lane, return couples
     * of null.
     */
    static MyPair<Lane, Lane> getFirstCollidingAndHigher(int oppositeAxis, Block block, Lanes lanes)
    {
        // Take lower coordinate of lane to getPos
        Double key = block.getBound().getPos(oppositeAxis);

        // Take lower lane which is maybe colliding with block
        Map.Entry<Double, Lane> floorLaneEntry = lanes.getFloorLaneEntry(key);

        // If lower lane exists
        if (floorLaneEntry != null)
        {
            // Get the first lane that is colliding
            Map.Entry<Double, Lane> firstColliding = getFirstLaneEndAfterKey(oppositeAxis, lanes, key, floorLaneEntry);
            if (firstColliding != null)
            {
                Lane higherThanCollidingLane = lanes.getHigherLane(firstColliding.getKey());

                // Then take lower lane that may be colliding with block from higher lane
                return new MyPair<Lane, Lane>(firstColliding.getValue(), higherThanCollidingLane);
            }
            else
            {
                return new MyPair<Lane, Lane>(null, null);
            }
        }
        else
        {
            // Lower lane doesn't exist, search for one higher lane that may be colliding
            Map.Entry<Double, Lane> higherLaneEntry = lanes.getHigherLaneEntry(key);

            // If lane exists and if the start of block is before the end of the lane
            if (higherLaneEntry != null && isKeyLowerOrEqualThanEndOfLane(oppositeAxis, key, higherLaneEntry.getValue()))
            {
                // Take this lane as the lower lane and get his higherLane
                Lane higherThanHigherLane = lanes.getHigherLane(higherLaneEntry.getKey());
                return new MyPair<Lane, Lane>(higherLaneEntry.getValue(), higherThanHigherLane);
            }
            else
            {
                // No first colliding lane
                return new MyPair<Lane, Lane>(null, null);
            }
        }
    }

    /**
     * @param oppositeAxis   opposite lane axis
     * @param lanes          collection of lanes
     * @param key            used key to check higher end of lane
     * @param startLaneEntry start lane to start parsing and associated key.
     *
     * @return startLane if startLane has the end after the key, or the higher one having the end after the key. If no
     * lanes have end after the key, return null.
     */
    private static Map.Entry<Double, Lane> getFirstLaneEndAfterKey(int oppositeAxis,
                                                                   Lanes lanes,
                                                                   Double key,
                                                                   Map.Entry<Double, Lane> startLaneEntry)
    {

        // If the key is before the end of the start lane
        if (isKeyLowerOrEqualThanEndOfLane(oppositeAxis, key, startLaneEntry.getValue()))
        {
            // We found our first lane with end after the key
            return startLaneEntry;
        }

        // Search for higher lane
        Map.Entry<Double, Lane> actualLane = lanes.getHigherLaneEntry(startLaneEntry.getKey());

        // While higher lane exists AND first colliding lane has not been found AND
        while (actualLane != null &&
                !isKeyLowerOrEqualThanEndOfLane(oppositeAxis, key, actualLane.getValue()))
        {
            // Get the higher lane
            actualLane = lanes.getHigherLaneEntry(actualLane.getKey());
        }

        // If we have found first lane with end of rect after the pos
        if (actualLane != null)
        {
            return actualLane;
        }
        else
        {
            // No corresponding lane found
            return null;
        }
    }

    /**
     * @param oppositeAxis opposite lanes axis
     * @param key          checked key
     * @param lane         used lane
     *
     * @return true if the key is before the end of the lane, false otherwise.
     */
    private static boolean isKeyLowerOrEqualThanEndOfLane(int oppositeAxis, Double key, Lane lane)
    {
        return key <= lane.getEndPos(oppositeAxis);
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
            return Direction.TOP;
        }
        else
        {
            // rect1 is after rect2
            return Direction.BOTTOM;
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
