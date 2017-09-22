package org.eadge.extractpdfexcel.tools;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.utils.MyUnique;
import org.eadge.extractpdfexcel.models.BlockMerger;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Merge blocks if they are too close, have at the least one same fontColor and one same backColor.
 */
public class DefaultBlockMerger extends BlockMerger
{
    /**
     * threshold factor distance used to know if two blocks are near
     */
    private double threshold_dist_factor;
    /**
     * threshold factor to allow alignment
     */
    private double threshold_align_factor;
    /**
     * max threshold to allow alignment
     */
    private double max_align_threshold;

    /**
     * @param threshold_dist_factor  threshold factor distance used to know if two blocks are near
     * @param threshold_align_factor threshold factor to allow alignment
     * @param max_align_threshold    max_align_threshold
     */
    public DefaultBlockMerger(double threshold_dist_factor, double threshold_align_factor, double max_align_threshold)
    {
        this.threshold_dist_factor = threshold_dist_factor;
        this.threshold_align_factor = threshold_align_factor;
        this.max_align_threshold = max_align_threshold;
    }

    public DefaultBlockMerger()
    {
        this(0.5, 0.1, 40.0);
    }

    /**
     * Check two by two, if blocks need to be merged and merged them.
     *
     * @param blocks list of blocks need to be checked and merged if needed
     */
    public void mergeIfNecessaryBlocks(Collection<Block> blocks)
    {
        if (blocks.size() < 2)
            return;

        Iterator<Block> iterator = blocks.iterator();

        Block first = iterator.next();
        Block second;
        while (iterator.hasNext())
        {
            second = first;
            first = iterator.next();

            MyUnique<Boolean> outputIsFirstLowerBlock = new MyUnique<>();
            if (needBlockMerging(threshold_dist_factor,
                                 threshold_align_factor,
                                 max_align_threshold,
                                 first,
                                 second,
                                 outputIsFirstLowerBlock))
            {
                // Merge blocks
                mergeBlock(outputIsFirstLowerBlock.getT(), first, second);

                // Remove first block
                iterator.remove();

                // first block become the mergeBlock (second block)
                first = second;
            }
        }
    }

    /**
     * Determine if two blocks respect merge conditions.
     *
     * @param threshold_dist_factor   threshold factor distance used to know if two blocks are near
     * @param threshold_align_factor  threshold factor to allow alignment
     * @param max_align_threshold     max threshold to allow alignment
     * @param first                   first block checked
     * @param second                  second block checked
     * @param outputIsFirstLowerBlock used to store lowerBlock comparaison result
     *
     * @return true if two blocks need to be merged, false if they don't
     */
    private static boolean needBlockMerging(double threshold_dist_factor,
                                            double threshold_align_factor,
                                            double max_align_threshold,
                                            Block first,
                                            Block second,
                                            MyUnique<Boolean> outputIsFirstLowerBlock)
    {
        // Axis of the line passing through the text
        int sameOrientationAxis = getSameOrientationAxis(first, second);

        // If they have not the same orientation
        if (sameOrientationAxis == -1)
        {
            return false;
        }

        // Axis perpendicular to the text line
        int oppositeAxis = getOppositeAxis(sameOrientationAxis);

        // Store block lower comparison
        outputIsFirstLowerBlock.setT(first.getPos(oppositeAxis) < second.getPos(oppositeAxis));

        Block higherBLock;
        Block lowerBlock;

        // If first is lower than second block
        if (outputIsFirstLowerBlock.getT())
        {
            lowerBlock = first;
            higherBLock = second;
        }
        else
        {
            lowerBlock = second;
            higherBLock = first;
        }

        return areAlign(sameOrientationAxis, lowerBlock, higherBLock, threshold_align_factor, max_align_threshold) &&
                areNear(oppositeAxis, threshold_dist_factor, lowerBlock, higherBLock) &&
                haveMatchingTypes(first, second);
    }

    /**
     * Merge two blocks, merge their text and rectangle.
     *
     * @param isRemovedBlockLower used test comparison to order contents
     * @param removedBlock        Block that will be removed, and contains first part.
     * @param mergeBlock          Block that will contain two blocks, and contains at input the second part.
     */
    private static void mergeBlock(Boolean isRemovedBlockLower, Block removedBlock, Block mergeBlock)
    {
        if (isRemovedBlockLower)
        {
            // Add the removed block content before merge block's contents
            mergeBlock.insertStartFormatted(removedBlock.getFormattedText());
            mergeBlock.insertStartOriginal(removedBlock.getOriginalText());
        }
        else
        {
            // Add the removed block content after merge block's contents
            mergeBlock.insertEndFormatted(removedBlock.getFormattedText());
            mergeBlock.insertEndOriginal(removedBlock.getOriginalText());
        }

        // Merge types
        mergeBlock.addAllBackColors(removedBlock.getBackColors());
        mergeBlock.addAllFontColors(removedBlock.getFontColors());
        mergeBlock.addAllFonts(removedBlock.getFonts());

        // Merge rectangle along x and y axis
        mergeBlockRect(0, removedBlock, mergeBlock);
        mergeBlockRect(1, removedBlock, mergeBlock);
    }

    /**
     * Get shared axis orientation. The axis orientation is the axis of the text line.
     *
     * @param first  first block checked
     * @param second second block checked
     *
     * @return orientation axis if they have the same orientation, or -1 if they have different orientation.
     */
    private static int getSameOrientationAxis(Block first, Block second)
    {
        // If they have the same text orientation
        if (first.getTextOrientation() != second.getTextOrientation() || first.getTextOrientation() == null)
            return -1;

        if (first.getBlockOrientation() != second.getBlockOrientation() || first.getBlockOrientation() == null)
            return -1;

        // Return the x or y orientation
        if (first.getBlockOrientation() == Direction.LEFT || first.getBlockOrientation() == Direction.RIGHT)
            return 0;
        else
            return 1;
    }

    private static int getOppositeAxis(int axis)
    {
        switch (axis)
        {
            case 0:
                return 1;
            case 1:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Check if two blocks are aligned on start, middle or end.
     *
     * @param axis                   used axis to check alignment
     * @param lowerBlock             first checked block
     * @param higherBlock            second checked block
     * @param threshold_align_factor threshold factor used to check align
     * @param max_align_threshold    the maximum threshold used for align detection
     *
     * @return true if blocks are aligned on Start, Mid, or End point, return false if they aren't aligned
     */
    private static boolean areAlign(int axis,
                                    Block lowerBlock,
                                    Block higherBlock,
                                    double threshold_align_factor,
                                    double max_align_threshold)
    {
        double threshold = Math.min(Math.max(lowerBlock.getPos(axis),
                                             higherBlock.getPos(axis)) * threshold_align_factor, max_align_threshold);

        double startDifference = lowerBlock.getPos(axis) - higherBlock.getPos(axis);

        // If two blocks are start aligned
        if (startDifference >= -threshold / 4 && startDifference < threshold / 2)
            return true;

        double middleDifference = Math.abs(lowerBlock.getMidPos(axis) - higherBlock.getMidPos(axis));

        // If two blocks are middle aligned
        if (middleDifference < threshold)
            return true;

        double endDifference = higherBlock.getEndPos(axis) - lowerBlock.getEndPos(axis);

        // If two blocks are end aligned
        if (endDifference >= -threshold / 4 && endDifference < threshold / 2)
            return true;

        // No alignement
        return false;
    }

    /**
     * Test if two blocks are near.
     *
     * @param axis                  used axis to check their distance
     * @param threshold_near_factor distance threshold factor on axis
     * @param lowerBlock            first block checked
     * @param higherBlock           second block checked
     *
     * @return true if first block is near to second block, return false if they aren't
     */
    private static boolean areNear(int axis, double threshold_near_factor, Block lowerBlock, Block higherBlock)
    {
        double dist = Math.abs(lowerBlock.getPos(axis) + lowerBlock.getLength(axis) - higherBlock.getPos(axis));

        return dist < threshold_near_factor * Math.min(lowerBlock.getLength(axis), higherBlock.getLength(axis));
    }

    /**
     * Test if 2 blocks have matching types
     *
     * @param first  first block checked
     * @param second second block checked
     *
     * @return true if they share one same color for back and font, return false if they don't
     */
    private static boolean haveMatchingTypes(Block first, Block second)
    {
        return containSameOnce(first.getBackColors(), second.getBackColors()) &&
                containSameOnce(first.getFontColors(), second.getFontColors());
    }

    /**
     * Merge blocks along one axis
     *
     * @param axis             used axis to merge
     * @param b1               first block
     * @param updatedBlockRect block which rect will be updated
     */
    private static void mergeBlockRect(int axis, Block b1, Block updatedBlockRect)
    {
        // Get lower start
        double start1   = b1.getPos(axis);
        double start2   = updatedBlockRect.getPos(axis);
        double startMin = Math.min(start1, start2);

        // Get higher end
        double end1   = b1.getPos(axis) + b1.getLength(axis);
        double end2   = updatedBlockRect.getPos(axis) + updatedBlockRect.getLength(axis);
        double endMax = Math.max(end1, end2);

        // Update rectangle
        updatedBlockRect.setPos(axis, start1);
        updatedBlockRect.setLength(axis, endMax - startMin);
    }

    /**
     * Test if one of the elements of c1 can be found in c2
     *
     * @param c1 first collection
     * @param c2 second collection
     *
     * @return true if one element or more of c1 has been found in c2
     */
    private static boolean containSameOnce(Collection c1, Collection c2)
    {
        if (c1.size() == 0)
            return (c2.size() == 0);

        for (Object element : c1)
        {
            if (c2.contains(element))
                return true;
        }
        return false;
    }
}
