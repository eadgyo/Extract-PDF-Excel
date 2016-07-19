package org.cora.extract_pdf_excel.data.lane;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by eadgyo on 12/07/16.
 */

/**
 * Keep lanes sorted
 */
public class Lanes
{
    private TreeMap<Double, Lane> lanes;

    public Lanes()
    {
        lanes = new TreeMap<Double, Lane>();
    }

    /**
     * Insert a lane using one key
     *
     * @param key used key for insertion
     * @param lane inserted
     */
    public void insertLane(double key, Lane lane)
    {
        lanes.put(key, lane);
    }


    /**
     * Insert lane using lower bound of opposite axis of lane direction
     *
     * @param oppositeAxis index of opposite axis
     * @param lane inserted ane
     */
    public void insertLane(int oppositeAxis, Lane lane)
    {
        lanes.put(lane.getPos(oppositeAxis), lane);
    }

    /**
     * Remove lane using lower bound as key
     *
     * @param key lower bound in opposite direction of lane
     * @return removed lane or null if key does not correspond to a lane
     */
    public Lane removeLane(Double key)
    {
        return lanes.remove(key);
    }

    /**
     * Remove lane using lower bound as key
     *
     * @param oppositeAxis index of opposite direction
     * @param lane to be removed
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
     * @return lower lane and his value in map
     */
    public Map.Entry<Double, Lane> getLowerLaneEntry(double key)
    {
        return lanes.lowerEntry(key);
    }

    /**
     * Get lane lower than key
     *
     * @param key to be checked
     * @return lower lane
     */
    public Lane getLowerLane(double key)
    {
        Map.Entry<Double, Lane> lowerLaneEntry = getLowerLaneEntry(key);

        return (lowerLaneEntry != null) ? lowerLaneEntry.getValue() : null;
    }

    /**
     * Get lane higher than key
     *
     * @param key to be checked
     * @return higher lane and his value in map
     */
    public Map.Entry<Double, Lane> getHigherLaneEntry(double key)
    {
        return lanes.higherEntry(key);
    }

    /**
     * Get lane higher than key
     *
     * @param key to be checked
     * @return higher lane
     */
    public Lane getHigherLane(double key)
    {
        Map.Entry<Double, Lane> higherLaneEntry = getHigherLaneEntry(key);

        return (higherLaneEntry != null) ? higherLaneEntry.getValue() : null;
    }

    /**
     * Replace key of one lane
     *
     * @param oppositeAxis opposite lane axis
     * @param oldKey old key in map
     * @param lane linked lane to key
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
}
