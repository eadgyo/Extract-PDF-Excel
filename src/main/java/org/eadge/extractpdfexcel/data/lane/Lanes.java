package org.eadge.extractpdfexcel.data.lane;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.exception.DifferentKeyLaneException;
import org.eadge.extractpdfexcel.exception.DuplicatedBlockException;
import org.eadge.extractpdfexcel.exception.NoCorrespondingLane;

import java.util.*;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Containing sorted lanes.
 */
public class Lanes
{
    private TreeMap<Double, Lane> lanes;

    public Lanes()
    {
        lanes = new TreeMap<Double, Lane>();
    }

    /**
     * Insert lane using lower bound of opposite axis of lane direction
     *
     * @param oppositeAxis index of opposite axis
     * @param lane         inserted ane
     */
    public void insertLane(int oppositeAxis, Lane lane)
    {
        lanes.put(lane.getPos(oppositeAxis), lane);
    }

    /**
     * Remove lane using lower bound as key
     *
     * @param oppositeAxis index of opposite direction
     * @param lane         to be removed
     *
     * @return removed lane or null if lane's key does not correspond to a lane
     */
    public Lane removeLane(int oppositeAxis, Lane lane)
    {
        return lanes.remove(lane.getPos(oppositeAxis));
    }

    /**
     * Get lane lower than key
     *
     * @param key to be checked
     *
     * @return lower lane
     */
    public Lane getLowerLane(double key)
    {
        Map.Entry<Double, Lane> lowerLaneEntry = getLowerLaneEntry(key);

        return (lowerLaneEntry != null) ? lowerLaneEntry.getValue() : null;
    }

    /**
     * Get lane lower than lane
     *
     * @param oppositeAxis index of opposite direction
     * @param lane         used lane
     *
     * @return lower lane
     */
    public Lane getLowerLane(int oppositeAxis, Lane lane)
    {
        Map.Entry<Double, Lane> lowerLaneEntry = getLowerLaneEntry(lane.getPos(oppositeAxis));

        return (lowerLaneEntry != null) ? lowerLaneEntry.getValue() : null;
    }

    /**
     * Get lane lower than lane
     *
     * @param oppositeAxis index of opposite direction
     * @param lane         used lane
     *
     * @return higher lane
     */
    public Lane getHigherLane(int oppositeAxis, Lane lane)
    {
        Map.Entry<Double, Lane> higherLaneEntry = getHigherLaneEntry(lane.getPos(oppositeAxis));

        return (higherLaneEntry != null) ? higherLaneEntry.getValue() : null;
    }

    /**
     * Get lane lower than key
     *
     * @param key to be checked
     *
     * @return lower lane and his value in map
     */
    public Map.Entry<Double, Lane> getLowerLaneEntry(double key)
    {
        return lanes.lowerEntry(key);
    }

    /**
     * Replace key of one lane
     *
     * @param oppositeAxis opposite lane axis
     * @param oldKey       old key in map
     * @param lane         linked lane to key
     */
    public void replaceKey(int oppositeAxis, double oldKey, Lane lane)
    {
        double newKey = lane.getPos(oppositeAxis);

        if (newKey != oldKey)
        {
            Lane removedLane = removeLane(oldKey);

            assert (removedLane == lane);

            lanes.put(newKey, lane);
        }
    }

    /**
     * Remove lane using lower bound as key
     *
     * @param key lower bound in opposite direction of lane
     *
     * @return removed lane or null if key does not correspond to a lane
     */
    public Lane removeLane(Double key)
    {
        return lanes.remove(key);
    }

    /**
     * Insert lane in lanes and fit the end of the lane to higher lane.
     *
     * @param oppositeAxis opposite lane axis
     * @param insertedLane inserted lane
     */
    public void insertLaneAndFitToHigher(int oppositeAxis, Lane insertedLane)
    {
        // Insert lane
        insertLane(insertedLane.getPos(oppositeAxis), insertedLane);

        // Get higher lane
        Lane higherLane = getHigherLane(insertedLane.getPos(oppositeAxis));

        // If higher lane exists
        if (higherLane != null)
        {
            // Fit end of inserted lane to higher lane if
            insertedLane.fitToHigherLane(oppositeAxis, higherLane);
        }
    }

    /**
     * Insert a lane using one key
     *
     * @param key  used key for insertion
     * @param lane inserted
     */
    public void insertLane(double key, Lane lane)
    {
        lanes.put(key, lane);
    }

    /**
     * Get lane higher than key
     *
     * @param key to be checked
     *
     * @return higher lane
     */
    public Lane getHigherLane(double key)
    {
        Map.Entry<Double, Lane> higherLaneEntry = getHigherLaneEntry(key);

        return (higherLaneEntry != null) ? higherLaneEntry.getValue() : null;
    }

    /**
     * Get lane higher than key
     *
     * @param key to be checked
     *
     * @return higher lane and his value in map
     */
    public Map.Entry<Double, Lane> getHigherLaneEntry(double key)
    {
        return lanes.higherEntry(key);
    }

    /**
     * Get the index of block in lanes
     *
     * @param oppositeAxis opposite lane axis
     * @param block        check block
     *
     * @return lane index containing block or -1 if block is not present or block's rectangle has no link.
     */
    public int getLaneIndexOfBlock(int oppositeAxis, Block block)
    {
        Lane blockLane = getFloorLane(block.getPos(oppositeAxis));
        if (blockLane == null)
            return -1;

        // Get the index of the block lane
        return getLaneIndex(blockLane);
    }

    /**
     * Get lane with key lower or equal to key
     *
     * @param key to be checked
     *
     * @return floor lane
     */
    public Lane getFloorLane(double key)
    {
        Map.Entry<Double, Lane> floorLaneEntry = getFloorLaneEntry(key);

        return (floorLaneEntry != null) ? floorLaneEntry.getValue() : null;
    }

    /**
     * Get lane with key higher or equal to key
     *
     * @param key to be checked
     *
     * @return ceiling lane
     */
    public Lane getCeilingLane(double key)
    {
        Map.Entry<Double, Lane> ceilingLaneEntry = getCeilingLaneEntry(key);

        return (ceilingLaneEntry != null) ? ceilingLaneEntry.getValue() : null;
    }

    /**
     * Get the index of the lane
     *
     * @param lane checked lane
     *
     * @return index or -1 if lane doesn't exist
     */
    public int getLaneIndex(Lane lane)
    {
        // Get the first element
        Map.Entry<Double, Lane> firstEntry = lanes.firstEntry();

        // If the first element is the searched one
        if (firstEntry.getValue() == lane)
            return 0;

        Map.Entry<Double, Lane> lastEntry       = lanes.lastEntry();
        Map.Entry<Double, Lane> actualEntry     = firstEntry;
        int                     actualLaneIndex = 0;

        // Parse the rest until we got the last
        while (actualEntry != lastEntry)
        {
            // Get the higher lane
            actualLaneIndex++;
            actualEntry = lanes.higherEntry(actualEntry.getKey());

            // If the lane is found
            if (actualEntry.getValue() == lane)
            {
                return actualLaneIndex;
            }
        }

        return -1;
    }

    /**
     * Get lane with key lower or equal to specified key
     *
     * @param key to be checked
     *
     * @return floor lane and his value in map
     */
    public Map.Entry<Double, Lane> getFloorLaneEntry(double key)
    {
        return lanes.floorEntry(key);
    }

    /**
     *  Get lane with key higher or equal to specified key
     *
     * @param key to be checked
     *
     * @return ceiling lane and his value in map
     */
    public Map.Entry<Double, Lane> getCeilingLaneEntry(double key)
    {
        return lanes.ceilingEntry(key);
    }

    /**
     * @return number of lanes
     */
    public int size()
    {
        return lanes.size();
    }

    /**
     * @return all lines in a collection
     */
    public Collection<Lane> getLanes()
    {
        return lanes.values();
    }

    /**
     * Get the size of each lane along the opposite axis of the lane.
     *
     * Use the start of the next lane as the end of the previous lane.
     *
     * @param oppositeAxis Opposite lane axis.
     *
     * @return size of each lane.
     */
    public ArrayList<Double> getLanesLength(int oppositeAxis)
    {
        ArrayList<Double> lanesBounds = new ArrayList<>();

        // Get the start of each lane from the sorted lane
        NavigableSet<Double> lanesStart = lanes.navigableKeySet();

        // Get the start
        Iterator<Double> iterator = lanesStart.iterator();

        // If the list of lanes is not empty
        if (iterator.hasNext())
        {
            Double endOfLane   = iterator.next();
            Double startOfLane;

            // The set is already ascendant sorted
            // Add each start - end size.
            while (iterator.hasNext())
            {
                startOfLane = endOfLane;
                endOfLane = iterator.next();

                lanesBounds.add(endOfLane - startOfLane);
            }

            // Add the size of the last lane
            double lastSize = lanes.lastEntry().getValue().getLength(oppositeAxis);

            lanesBounds.add(lastSize);
        }
        return lanesBounds;
    }

    /**
     * Get contained lanes keeping sorted order
     *
     * @return set lane keeping sorted order
     */
    public Set<Map.Entry<Double, Lane>> getSortedLanes()
    {
        return lanes.entrySet();
    }

    /**
     * Check lane rectangle pos corresponds to his key
     * @param oppositeAxis opposite lane axis
     */
    public void checkLaneAndAssociatedKey(int oppositeAxis) throws DifferentKeyLaneException
    {
        for (Map.Entry<Double, Lane> doubleLaneEntry : lanes.entrySet())
        {
            Double key = doubleLaneEntry.getKey();
            double expected = doubleLaneEntry.getValue().getPos(oppositeAxis);

            if (key != expected)
                throw new DifferentKeyLaneException();
        }
    }

    public void checkBlocksAllContains(Collection<Block> blocks) throws DuplicatedBlockException, NoCorrespondingLane
    {
        // Create a set to checked remove blocks
        Set<Block> blocksSet = new HashSet<>(blocks);

        // For each lane
        for (Lane lane : lanes.values())
        {
            Collection<Block> blocksCollection = lane.getBlocksCollection();

            for (Block block : blocksCollection)
            {
                // Remove block
                boolean removed = blocksSet.remove(block);

                // Check if the block was already deleted, means the block is also in another lane
                if (!removed)
                    throw new DuplicatedBlockException();

            }
        }

        // If there are still blocks in set, there are blocks not contained
        if (blocksSet.size() != 0)
            throw new NoCorrespondingLane();
    }
}
