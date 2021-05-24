package org.eadge.extractpdfexcel.process.arrangement;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.CollisionState;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.data.lane.Lane;

import java.util.Map;

/**
 * Created by eadgyo on 20/07/16.
 * <p/>
 * Tools to handle lane/lane and block/lane collisions.
 */
class CollisionTools
{

    /**
     * Test if a block is colliding with a lane
     *
     * @param oppositeAxis opposite axis of lane
     * @param block        tested block
     * @param lane         tested lane
     *
     * @return true if they are colliding, false if they aren't
     */
    static boolean isCollidingWithBlock(int oppositeAxis, Block block, Lane lane)
    {
        return areRectColliding(oppositeAxis, block.getBound(), lane.getBound());
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
    public static boolean areRectColliding(int oneAxis, Rectangle2 rect1, Rectangle2 rect2)
    {
        return rect1.getPos(oneAxis) < rect2.getPos(oneAxis) + rect2.getLength(oneAxis) &&
                rect1.getPos(oneAxis) + rect1.getLength(oneAxis) > rect2.getPos(oneAxis);
    }

    /**
     * Get in lane block colliding with block
     *
     * @param axis  opposite lane axis
     * @param block checked block
     *
     * @return block that is colliding with checked block, or null if there are no blocks in collision.
     */
    static Block getBlockCollidingInLane(int axis, Block block, Lane lane)
    {
        // Get higher block that is not colliding
        Map.Entry<Double, Block> mayCollidingBlockEntry = lane.getHigherBlockEntry(block.getPos(axis));

        // If higher is not colliding, try to get lower blocks that may collide on axis lane
        if (mayCollidingBlockEntry == null)
        {
            mayCollidingBlockEntry = lane.getFloorBlockEntry(block.getPos(axis));
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
    private static CollisionState getStateColliding(int oneAxis, Rectangle2 rect1, Rectangle2 rect2)
    {
        // If rect1 is before rect2
        if (rect1.getPos(oneAxis) + rect1.getLength(oneAxis) <= rect2.getPos(oneAxis))
            return CollisionState.NO_COLLISION_RIGHT;

        // If rect1 is after rect2
        if (rect1.getPos(oneAxis) >= rect2.getPos(oneAxis) + rect2.getLength(oneAxis))
            return CollisionState.NO_COLLISION_LEFT;

        // Rect1 and rect2 are in collision
        return CollisionState.IN_COLLISION;
    }

}
