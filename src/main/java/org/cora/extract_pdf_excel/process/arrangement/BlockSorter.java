package org.cora.extract_pdf_excel.process.arrangement;

import org.cora.extract_pdf_excel.data.MyPair;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.block.Direction;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;

import java.util.HashSet;
import java.util.Set;

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
        MyPair<Lane, Lane> lowerAndHigherLane = LaneTools.getLowerAndHigherLane(oppositeAxis, block, lanes);
        Lane               lowerLane          = lowerAndHigherLane.getLeft();
        Lane               higherLane         = lowerAndHigherLane.getRight();

        // If lowerLane exists AND lowerLane is colliding
        if (lowerLane != null && CollisionTools.isCollidingWithBlock(oppositeAxis, block, lowerLane))
        {
            // Compare block to existing in lane blocks on lane axis
            Block      collidingBlock      = CollisionTools.getBlockCollidingInLane(axis, block, lowerLane);
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
                collidingBlock = CollisionTools.getBlockCollidingInLane(axis, block, lowerLane);
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
                    CollisionTools.isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
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
            if (higherLane == null || !CollisionTools.isCollidingWithBlock(oppositeAxis, higherBlock, higherLane))
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
            LaneTools.insertBlocksInRightLane(axis, oppositeAxis, savedBlocks, lowerLane, higherLane);
        }
        else if (forceResizingLowerLane)
        {
            // HigherLane is not colliding with LowerLane.
            // But force resize lower lane using in blocks
            lowerLane.resetAndFitLaneToInBlocks(axis, oppositeAxis);
        }

        return higherLane;
    }
}
