package org.eadge.extractpdfexcel.models;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Parameters used to identify separation of blocks.
 */
public class TextBlockIdentifier
{
    /**
     * Used to determine different block in extract process.
     *
     * Above threshold, two letters are considered on different lines
     */
    public final double sameLineThreshold;

    /**
     * Used to detect different block in extract process.
     *
     * If space between two letters are above characterSpaceSize * sameBlockFactorThreshold, it's a different block.
     */
    public final double sameBlockFactorThreshold;

    /**
     * Used to detect space character in extract process.
     *
     * If space is below characterSpaceSize * sameBlockFactorThreshold and above spaceBlockFactorThreshold, it's a space
     * character.
     */
    public final double spaceBlockFactorThreshold;

    /**
     * Used to determine if a block is facing Y or X.
     *
     * We take two extremes points of blocks center line and compare coordinates. If difference is under
     * thresholdAlongY, block is facing toward Y axis. Else if difference is above thresholdAlongY, block is facing X
     * axis.
     */
    public final double thresholdAlongY;

    public TextBlockIdentifier(double sameLineThreshold,
                               double sameBlockFactorThreshold,
                               double spaceBlockFactorThreshold,
                               double thresholdAlongY)
    {
        this.sameLineThreshold = sameLineThreshold;
        this.sameBlockFactorThreshold = sameBlockFactorThreshold;
        this.spaceBlockFactorThreshold = spaceBlockFactorThreshold;
        this.thresholdAlongY = thresholdAlongY;
    }

    public TextBlockIdentifier()
    {
        this.sameLineThreshold = 1.0f;
        this.sameBlockFactorThreshold = 3.0f;
        this.spaceBlockFactorThreshold = 2.0f;
        this.thresholdAlongY = 0.00001f;
    }
}
