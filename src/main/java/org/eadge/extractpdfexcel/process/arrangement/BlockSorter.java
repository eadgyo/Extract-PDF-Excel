package org.eadge.extractpdfexcel.process.arrangement;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.data.lane.Lanes;
import org.eadge.extractpdfexcel.data.utils.MyPair;

import java.util.*;

/**
 * Created by eadgyo on 17/07/16.
 * <p/>
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
        MyPair<Lane, Lane> floorAndHigherLane = LaneTools.getFirstCollidingAndHigher(oppositeAxis, block, lanes);

        Lane lowerLane  = floorAndHigherLane.getLeft();
        Lane higherLane = floorAndHigherLane.getRight();

        // If lowerLane exists AND lowerLane is colliding
        if (lowerLane != null &&
                CollisionTools.isCollidingWithBlock(oppositeAxis, block, lowerLane))
        {
            // Compare block to existing in lane blocks on lane axis
            Block      collidingBlock      = CollisionTools.getBlockCollidingInLane(axis, block, lowerLane);
            Set<Block> savedCollidingBlock = new HashSet<>();

            if (collidingBlock != null && CollisionTools.areRectColliding(oppositeAxis, block.getBound(), collidingBlock.getBound()))
            {
                // We are in a case were the 2 blocks are colliding, no decision made
            }
            else
            {

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

                    // Update lanes' bounds
                    updateLowerAndKnownHigher(oppositeAxis, lanes, lowerLane, higherLane);

                    // Update collidingBlock
                    collidingBlock = CollisionTools.getBlockCollidingInLane(axis, block, lowerLane);
                }
            }

            // Insert block in lowerLane
            addBlockFitAndUpdateKey(axis, oppositeAxis, lanes, lowerLane, block);

            // Update lanes' bounds
            updateLowerAndKnownHigher(oppositeAxis, lanes, lowerLane, higherLane);

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

            // Init lane rectangle with block rectangle
            lane.setRectangle(block.getBound());

            // Add block in lane
            lane.addBlock(axis, block);

            // Insert block in created lane
            lanes.insertLaneAndFitToHigher(oppositeAxis, lane);
        }
    }

    /**
     * Insert block in one lane and update lane key in lanes.
     * @param axis lane axis
     * @param oppositeAxis opposite lane axis
     * @param lanes collection of sorted lanes
     * @param addingLane lane in which we add block
     * @param block added block
     */
    private static void addBlockFitAndUpdateKey(int axis, int oppositeAxis, Lanes lanes, Lane addingLane, Block block)
    {
        // Save key to update lane key
        double savedKey = addingLane.getPos(oppositeAxis);

        // Add block and update lane bounds
        addingLane.addBlockAndFitLane(axis, oppositeAxis, block);

        // Update key
        lanes.replaceKey(oppositeAxis, savedKey, addingLane);
    }

    /**
     * Get lower lane from lanes and update bounds of lower and middle lanes.
     * @param oppositeAxis opposite lane axis
     * @param lanes collection of sorted lanes
     * @param middle middle lane
     * @param higherLane higher lane
     */
    private static void updateLowerAndKnownHigher(int oppositeAxis, Lanes lanes, Lane middle, Lane higherLane)
    {
        // Get lowerLane before changing middle key
        Lane lowerLane = lanes.getLowerLane(oppositeAxis, middle);

        // Update middle lane using higher bounds
        if (higherLane != null)
        {
            assert (middle != higherLane);

            // Save middle key
            double savedMiddleKey = middle.getPos(oppositeAxis);

            // Fit middle to higher lane
            middle.fitToHigherLane(oppositeAxis, higherLane);

            // Update middle key
            lanes.replaceKey(oppositeAxis, savedMiddleKey, middle);
        }

        // Update lower lane using middle bounds
        if (lowerLane != null)
        {
            assert (middle != lowerLane);
            assert (lowerLane != higherLane);

            // Save lower key
            double savedLowerKey  = lowerLane.getPos(oppositeAxis);

            // Fit lower to middle lane
            lowerLane.fitToHigherLane(oppositeAxis, middle);

            // Update lower key
            lanes.replaceKey(oppositeAxis, savedLowerKey, lowerLane);
        }
    }

    /**
     * Get lower lane from lanes and update bounds of lower and middle lanes.
     * @param oppositeAxis opposite lane axis
     * @param lanes collection of sorted lanes
     * @param middle middle lane
     */
    private static void updateLowerAndHigher(int oppositeAxis, Lanes lanes, Lane middle)
    {
        // Update middle lane using higher bounds
        Lane higherLane = lanes.getHigherLane(oppositeAxis, middle);

        updateLowerAndKnownHigher(oppositeAxis, lanes, middle, higherLane);
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
                                               Lanes lanes,
                                               Set<Block> savedCollidingBlocks)
    {
        // Take the inserted block or existing block, with higher coordinate
        // Get position of inserted block relative to colliding block
        Direction relativeDirection = LaneTools.getRelativeDirection(oppositeAxis,
                                                                     insertedBlock.getBound(),
                                                                     collidingBlock.getBound());
        Block lowerBlock, higherBlock;

        // If the inserted block is at the right of colliding block
        if (relativeDirection == Direction.TOP)
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

            // Save colliding block for future reinsert, preventing collision with higherLane's blocks
            savedCollidingBlocks.add(collidingBlock);

            // If higher lane exists and is colliding with higherBlock
            if (higherLane != null &&
                    CollisionTools.isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
            {
                // Don't create a new lane

                // Save higherLane to update his key in lanes
                double oldHigherKey = higherLane.getPos(oppositeAxis);

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
                Lane createdLane = splitLowerLaneAndReorderBlocks(axis,
                                                           oppositeAxis,
                                                           lowerBlock,
                                                           higherBlock,
                                                           lowerLane,
                                                           true);

                // Insert created higherLane
                lanes.insertLaneAndFitToHigher(oppositeAxis, createdLane);
            }

            // Update lower key
            lanes.replaceKey(oppositeAxis, oldLowerKey, lowerLane);

            // Use lowerLane to insert inserted block
            return lowerLane;
        }
        else
        {
            lowerBlock = collidingBlock;
            higherBlock = insertedBlock;

            // Do not need to remove higherBlock as it's not in a lane

            // If higher lane does not exist or is not colliding with higherBlock
            if (higherLane == null || !CollisionTools.isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
            {
                // Save lowerLane to update his key in lanes
                double oldLowerKey = lowerLane.getPos(oppositeAxis);

                // Split lower lane and reorder lower lane's blocks
                higherLane = splitLowerLaneAndReorderBlocks(axis,
                                                            oppositeAxis,
                                                            lowerBlock,
                                                            higherBlock,
                                                            lowerLane,
                                                            false);

                // Insert created higherLane
                lanes.insertLaneAndFitToHigher(oppositeAxis, higherLane);

                // Update lower key
                lanes.replaceKey(oppositeAxis, oldLowerKey, lowerLane);
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
        // Create a new lane and insert higher block in lanes
        Lane higherLane = new Lane();

        // Init higher lane bound
        higherLane.setRectangle(higherBlock.getBound());


        // If the end of lower lane is after start of higher lane, lower and higher lane are colliding
        if (lowerLane.getEndPos(oppositeAxis) > higherLane.getPos(oppositeAxis))
        {
            // Need to fits lanes and reinsert all blocks

            // Save lower lane
            Set<Block> savedBlocks = lowerLane.copyBlocks();

            // Clear blocks in lane
            lowerLane.clearBlocks();

            // Reset lane bound to lowerBlock
            lowerLane.setRectangle(lowerBlock.getBound());

            // Reinsert all saved blocks in lowerLane or higherLane
            LaneTools.insertBlocksInRightLane(axis, oppositeAxis, savedBlocks, lowerLane, higherLane);

            // Update end of lower lane to created higher lane
            lowerLane.fitToHigherLane(oppositeAxis, higherLane);
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
     * Move block to higher lane, if colliding percent between block and higher lane is higher than block and actual
     * block lane. Also check if it's not colliding with an higher block.
     *
     * @param axis lane axis
     * @param oppositeAxis opposite lane axis
     * @param lanes collection of sorted lanes
     */
    public static void reinsertBlockMoreCollidingHigherLane(int axis, int oppositeAxis, Lanes lanes)
    {
        Collection<Lane> laneCollection = lanes.getLanes();

        // If there is less than two lanes
        if (laneCollection.size() < 2)
        {
            // No higher lanes exist
            return;
        }

        Iterator<Lane> iteratorLane = laneCollection.iterator();

        Lane higherLane = iteratorLane.next();

        while (iteratorLane.hasNext())
        {
            // Get actual lane and his higher lane
            Lane actualLane = higherLane;
            higherLane = iteratorLane.next();

            // Parse blocks of actual lane
            Collection<Block> blocksCollection = actualLane.getBlocksCollection();
            for (Iterator<Block> iteratorBlocks = blocksCollection.iterator(); iteratorBlocks.hasNext(); )
            {
                Block block = iteratorBlocks.next();
                // If the higher lane is more colliding than the actual lane
                if (isHigherLaneMoreColliding(oppositeAxis, actualLane, higherLane, block))
                {
                    // Check if there are no blocks colliding
                    Block collidingBlock = CollisionTools.getBlockCollidingInLane(axis, block, higherLane);
                    if (collidingBlock == null)
                    {
                        // Remove block from lane.
                        iteratorBlocks.remove();

                        // Insert block in higher lane. Don't need to update higher lane lower bound.
                        higherLane.addBlock(axis, block);
                    }
                }
            }
        }
    }

    /**
     * Check if a block is more colliding with higher or lowerLane
     * @param oppositeAxis opposite lane axis
     * @param lowerLane lower lane
     * @param higherLane higher lane
     * @param block checked block
     * @return true if higher lane is more colliding with block than the lower lane, false otherwise.
     */
    public static boolean isHigherLaneMoreColliding(int oppositeAxis, Lane lowerLane, Lane higherLane, Block block)
    {
        // If the block is not in collision with the higher block
        if (block.getEndPos(oppositeAxis) < higherLane.getPos(oppositeAxis))
        {
            // The colliding surface is 0.
            // The lower lane is more colliding
            return false;
        }

        // If the block is exceeding higher lane
        if (block.getEndPos(oppositeAxis) >= higherLane.getEndPos(oppositeAxis))
        {
            // Block is exceeding limits
            return false;
        }

        // Block must at least be in lower lane and not only in higher lane
        assert (lowerLane.getPos(oppositeAxis) - block.getPos(oppositeAxis) <= 0.0001f);
        assert (block.getPos(oppositeAxis) - higherLane.getPos(oppositeAxis) <= 0.0001f);

        // Compute lower colliding surface
        double lowerCollidingSurface = lowerLane.getEndPos(oppositeAxis) - block.getPos(oppositeAxis);

        // Compute higher colliding surcace
        double higherCollidingSurface = block.getEndPos(oppositeAxis) - higherLane.getPos(oppositeAxis);

        return lowerCollidingSurface < higherCollidingSurface;
    }
}
