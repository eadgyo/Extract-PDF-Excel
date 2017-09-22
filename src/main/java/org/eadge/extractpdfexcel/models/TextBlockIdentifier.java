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
     * If space between two letters are above characterSpaceSize * sameBlockFactorX, it's a different block.
     */
    public final double sameBlockFactorX;

    /**
     * Used to detect space character in extract process.
     *
     * If space is below characterSpaceSize * sameBlockFactorX and above spaceBlockFactorX, it's a space
     * character.
     */
    public final double spaceBlockFactorX;

    /**
     * Define space factor between block to consider as single block
     */
    public final double mergeFactor;

    /**
     * If true, removed duplicated block.
     */
    public final boolean cleanDuplicated;

    /**
     * Used to determine if a block is facing Y or X.
     *
     * We take two extremes points of blocks center line and compare coordinates. If difference is under
     * thresholdAlongY, block is facing toward Y axis. Else if difference is above thresholdAlongY, block is facing X
     * axis.
     */
    public final double thresholdAlongY;

    public TextBlockIdentifier(double sameLineThreshold,
                               double sameBlockFactorX,
                               double spaceBlockFactorX,
                               double thresholdAlongY,
                               double mergeFactor,
                               boolean cleanDuplicated)
    {
        this.sameLineThreshold = sameLineThreshold;
        this.sameBlockFactorX = sameBlockFactorX;
        this.spaceBlockFactorX = spaceBlockFactorX;
        this.thresholdAlongY = thresholdAlongY;
        this.mergeFactor = mergeFactor;
        this.cleanDuplicated = cleanDuplicated;
    }

    public TextBlockIdentifier()
    {
        this.sameLineThreshold = 1.0f;
        this.sameBlockFactorX = 3.0f;
        this.spaceBlockFactorX = 2.0f;
        this.thresholdAlongY = 0.00001f;
        this.mergeFactor = 1.4f;
        this.cleanDuplicated = true;
    }
}
