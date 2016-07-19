package org.cora.extract_pdf_excel.process.arrangement;

import org.cora.extract_pdf_excel.data.MyPair;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.block.CollisionState;
import org.cora.extract_pdf_excel.data.block.Direction;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;
import org.cora.extract_pdf_excel.data.lane.Rect;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by eadgyo on 17/07/16.
 */

/**
 * Tools to sort blocks in lane
 *
 * Use oppositeAxis for lane insertion in lanes
 * Use axis for block insertion in lane
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
        // Get lowerLane and higherLane from block
        MyPair<Lane, Lane> lowerAndHigherLane = getLowerAndHigherLane(oppositeAxis, block, lanes);
        Lane               lowerLane          = lowerAndHigherLane.getLeft();
        Lane               higherLane         = lowerAndHigherLane.getRight();

        // If lowerLane exists AND lowerLane is colliding
        if (lowerLane != null && isCollidingWithBlock(oppositeAxis, block, lowerLane))
        {
            // Compare block to existing in lane blocks on lane axis
            Block      collidingBlock      = getBlockCollidingInLane(axis, block, lowerLane);
            Set<Block> savedCollidingBlock = new HashSet<>();

            // While there are blocks colliding along lane axis
            while (collidingBlock != null)
            {
                // We have to find another lane to handle prevent collision
                // If a good higher lane exists, use this one, else split lower lane in 2 lanes
                Lane insertedLane = useExistingOrSplitLane(axis,
                                                           oppositeAxis,
                                                           block,
                                                           collidingBlock,
                                                           lowerLane,
                                                           higherLane,
                                                           lanes,
                                                           savedCollidingBlock);
                // If insert lane has changed
                if (insertedLane != lowerLane)
                {
                    // Change lowerLane
                    lowerLane = insertedLane;

                    // Update higherLane
                    higherLane = lanes.getHigherLane(lowerLane.getPos(oppositeAxis));
                }

                // Update collidingBlock
                collidingBlock = getBlockCollidingInLane(axis, block, lowerLane);
            }

            // Insert block in lowerLane
            lowerLane.addBlockAndFitLane(axis, oppositeAxis, block);
            // Make sure lowerLane is not colliding with his higherLane
            lowerLane.fitToHigherLane(oppositeAxis, higherLane);

            // Reinsert all removed blocks
            for (Block removedBlock : savedCollidingBlock)
            {
                insertInLanes(axis, oppositeAxis, removedBlock, lanes);
            }
        }
        else
        {
            // Correct lane does no exist, we have to create a new Lane

            // Create a new lane with block rect as lane bound
            Lane lane = new Lane();
            // Force use of block
            lane.setRectangle(block.getBound());

            // Insert block in created lane
            lanes.insertLaneAndFitToHigher(oppositeAxis, lane);
        }

    }

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
    private static MyPair<Lane, Lane> getLowerAndHigherLane(int oppositeAxis, Block block, Lanes lanes)
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
     * Test if a block is colliding with a lane
     *
     * @param oppositeAxis opposite axis of lane
     * @param block        tested block
     * @param lane         tested lane
     *
     * @return true if they are colliding, false if they aren't
     */
    private static boolean isCollidingWithBlock(int oppositeAxis, Block block, Lane lane)
    {
        return areRectColliding(oppositeAxis, block.getBound(), lane.getBound());
    }

    /**
     * Get in lane block colliding with block
     *
     * @param axis  opposite lane axis
     * @param block checked block
     *
     * @return block that is colliding with checked block, or null if there are no blocks in collision.
     */
    private static Block getBlockCollidingInLane(int axis, Block block, Lane lane)
    {
        // Get higher block that is not colliding
        Map.Entry<Double, Block> mayCollidingBlockEntry = lane.getHigherBlockEntry(block.getPos(axis));

        // If higher is not colliding, try to get lower blocks that may collide on axis lane
        if (mayCollidingBlockEntry == null)
        {
            mayCollidingBlockEntry = lane.getLowerBlockEntry(block.getPos(axis));
        }

        CollisionState lastCollisionState = CollisionState.NO_COLLISION_LEFT;
        // While there are still blocks at left AND
        // second block is not colliding AND he is not at left of first block (checked block)
        while (mayCollidingBlockEntry != null &&
                (lastCollisionState =
                        getStateColliding(axis,
                                          block.getBound(),
                                          mayCollidingBlockEntry.getValue()
                                                                .getBound())) == CollisionState.NO_COLLISION_RIGHT)
        {
            // Still not found colliding block nor end of possibles colliding blocks
            // So get left block, that is may be colliding with checked block
            mayCollidingBlockEntry = lane.getLowerBlockEntry(mayCollidingBlockEntry.getKey());
        }

        // If last state collision was collision
        if (lastCollisionState == CollisionState.IN_COLLISION)
            return mayCollidingBlockEntry.getValue(); // block in collision
        else // No blocks in Collision
            return null;
    }

    /**
     * Use higher lane or create a new lane to prevent collision between block and collidingBlock
     * Used to prevent collision of two blocks.
     *
     * @param axis                 lane axis
     * @param oppositeAxis         opposite lane axis
     * @param insertedBlock        inserted block
     * @param collidingBlock       existing block
     * @param lowerLane            lower than higherLane
     * @param higherLane           higher than lowerLane
     * @param lanes                multiples lanes
     * @param savedCollidingBlocks saved removed blocks for future reinsertion
     *
     * @return lane to insert the block
     */
    private static Lane useExistingOrSplitLane(int axis,
                                               int oppositeAxis,
                                               Block insertedBlock,
                                               Block collidingBlock,
                                               Lane lowerLane,
                                               Lane higherLane,
                                               Lanes lanes, Set<Block> savedCollidingBlocks)
    {
        // Take the inserted block or existing block, with higher coordinate
        // Get position of inserted block relative to colliding block
        Direction relativeDirection = getRelativeDirection(oppositeAxis,
                                                           insertedBlock.getBound(),
                                                           collidingBlock.getBound());
        Block lowerBlock, higherBlock;

        // If the inserted block is at the right of the colliding block
        if (relativeDirection == Direction.LEFT)
        {
            lowerBlock = insertedBlock;
            higherBlock = collidingBlock;

            // Save lowerLane to update his key in lanes
            double oldLowerKey = lowerLane.getPos(oppositeAxis);

            // Colliding block have to change lane
            // Remove first collidingBlock from his lane
            Block removed = lowerLane.remove(higherBlock.getPos(axis));
            // Make sure colliding block key pointed to himself in his lane
            assert (removed == higherBlock);

            // If higher lane exists and is colliding with higherBlock
            if (higherLane != null &&
                    isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
            {
                // Don't create a new lane

                // Save higherLane to update his key in lanes
                double oldHigherKey = higherLane.getPos(oppositeAxis);

                // Save colliding block for future reinsert, preventing collision with higherLane's blocks
                savedCollidingBlocks.add(collidingBlock);

                // Update lowerLane size to update lane bounds after removing colliding block
                lowerLane.resetAndFitLaneToInBlocks(axis, oppositeAxis);
                // Prevent lane collision by fitting end of lowerLane to start of higherLane
                lowerLane.fitToHigherLane(oppositeAxis, higherLane);

                // If higherLane key has changed
                if (oldHigherKey != higherLane.getPos(oppositeAxis))
                {
                    // Update his key in lanes
                    lanes.replaceKey(oppositeAxis, oldHigherKey, higherLane);
                }
            }
            else
            {
                // Can't add collidingBlock in higherLane because higherLane is after collidingBlock along
                // oppositeAxis.
                // Create a new lane to insert collidingBlock
                splitLowerLaneAndReorderBlocks(axis, oppositeAxis, lowerBlock, higherBlock, lowerLane, true);
            }

            // Use lowerLane to insert inserted block
            return lowerLane;
        }
        else
        {
            lowerBlock = collidingBlock;
            higherBlock = insertedBlock;

            // Do not need to remove higherBlock as it's not in a lane

            // If higher lane does not exist or is not colliding with higherBlock
            if (higherLane == null || !isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
            {
                // Split lower lane and reorder lower lane's blocks
                higherLane = splitLowerLaneAndReorderBlocks(axis,
                                                            oppositeAxis,
                                                            lowerBlock,
                                                            higherBlock,
                                                            lowerLane,
                                                            false);
            }

            // Use higherLane to insert inserted block
            return higherLane;
        }
    }

    /**
     * Split lowerLane and reorder lower lane in blocks
     *
     * @param axis                   lane axis
     * @param oppositeAxis           opposite lane axis
     * @param lowerBlock             lower than higher block
     * @param higherBlock            higher than lower block
     * @param lowerLane              lower lane splitted in lanes
     * @param forceResizingLowerLane force update the size of lowerLane
     *
     * @return created higher lane
     */
    private static Lane splitLowerLaneAndReorderBlocks(int axis,
                                                       int oppositeAxis,
                                                       Block lowerBlock,
                                                       Block higherBlock,
                                                       Lane lowerLane,
                                                       boolean forceResizingLowerLane)
    {
        // HigherLane cannot be used to reinsert higherBlock
        // Create a new lane and insert it in lanes
        Lane higherLane = new Lane();
        higherLane.fitLane(axis, oppositeAxis, higherBlock);

        // If the start of lower lane is before end of lowerLane, there are colliding
        if (lowerLane.getPos(oppositeAxis) + lowerLane.getLength(oppositeAxis) > higherLane.getPos(oppositeAxis))
        {
            // Need to reinsert all blocks

            // Save lower lane
            Set<Block> savedBlocks = lowerLane.copyBlocks();

            // Clear blocks in lane
            lowerLane.clearBlocks();
            // Reset lane bound to lowerBlock
            lowerLane.setRectangle(lowerBlock.getBound());

            // Reinsert all saved blocks in lowerLane or higherLane
            insertBlocksInRightLane(axis, oppositeAxis, savedBlocks, lowerLane, higherLane);
        }
        else if (forceResizingLowerLane)
        {
            // HigherLane is not colliding with LowerLane.
            // But force resize lower lane using in blocks
            lowerLane.resetAndFitLaneToInBlocks(axis, oppositeAxis);
        }

        return higherLane;
    }

    /**
     * Test if one rectangle is colliding with another rectangle on one axis
     *
     * @param oneAxis used axis
     * @param rect1   first rectangle
     * @param rect2   second rectangle
     *
     * @return true if they are colliding, false if they aren't
     */
    private static boolean areRectColliding(int oneAxis, Rect rect1, Rect rect2)
    {
        return rect1.getPos(oneAxis) < rect2.getPos(oneAxis) + rect2.getLength(oneAxis) &&
                rect1.getPos(oneAxis) + rect1.getLength(oneAxis) > rect2.getPos(oneAxis);
    }

    /**
     * Return state of collision.
     *
     * <p>
     * There are 3 possibles states of collisions. IN_COLLISION state is when two blocks are colliding.
     * NO_COLLISION_, means that there are no collision follow by the direction of the first rectangle.
     * NO_COLLISION_LEFT is when the first rectangle is at the left of the second rectangle. NO_COLLISION_RIGHT is
     * when the first rectangle is at the right of the second rectangle.
     * </p>
     *
     * @param oneAxis used axis
     * @param rect1   first rectangle
     * @param rect2   second rectangle
     *
     * @return 2 rectangles collision state
     */
    private static CollisionState getStateColliding(int oneAxis, Rect rect1, Rect rect2)
    {
        // If rect1 is before rect2
        if (rect1.getPos(oneAxis) + rect1.getLength(oneAxis) <= rect2.getPos(oneAxis))
            return CollisionState.NO_COLLISION_LEFT;

        // If rect1 is after rect2
        if (rect1.getPos(oneAxis) >= rect2.getPos(oneAxis) + rect2.getLength(oneAxis))
            return CollisionState.NO_COLLISION_RIGHT;

        // Rect1 and rect2 are in collision
        return CollisionState.IN_COLLISION;
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
    private static Direction getRelativeDirection(int oneAxis, Rect rect1, Rect rect2)
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

    private static void insertBlocksInRightLane(int axis,
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

    /**
     * Add a block in lane and fit lane with block and with existing lanes
     *
     * @param axis         lane axis
     * @param oppositeAxis opposite lane axis
     * @param block        added block
     * @param lane         inserting lane
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
