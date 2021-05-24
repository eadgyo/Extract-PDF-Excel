package org.eadge.extractpdfexcel.tools;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.parser.*;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;


/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Extract blocks from pdf file.
 */
public class DefaultSimpleExtractor implements TextExtractionStrategy
{

    private boolean isFirstRender = true;

    /**
     * Hold start and end line text.
     */
    private Vector lastStart = null;
    private Vector endLine   = null;

    /**
     * Hold upper and lower position of text.
     */
    private Vector lastAscent  = null;
    private Vector lastDescent = null;

    /**
     * Holds current start position of text.
     */
    private Vector startLine = null;

    /**
     * Extracted text temp.
     */
    private StringBuilder result = new StringBuilder();

    /**
     * Holds all text info stored for one block.
     */
    private ArrayList<TextRenderInfo> blockTextInfos = new ArrayList<>();

    /**
     * List of extracted blocks.
     */
    private ArrayList<Block> extractedBlocks = new ArrayList<>();

    /**
     * Hold number of text along x or along y to determine pdf orientation.
     * We estimate this direction by taking the direction of most of block.
     */
    private int nBlockFacingTop    = 0;
    private int nBlockFacingBottom = 0;
    private int nBlockFacingLeft   = 0;
    private int nBlockFacingRight  = 0;

    /**
     * Hold parameters used to separate blocks and create spaces.
     */
    private TextBlockIdentifier textBlockIdentifier;

    public DefaultSimpleExtractor(TextBlockIdentifier textBlockIdentifier)
    {
        this.textBlockIdentifier = textBlockIdentifier;
    }

    public String getResultantText()
    {
        return result.toString();
    }

    public void beginTextBlock()
    {

    }

    private void appendTextChunk(CharSequence text)
    {
    }

    public void renderText(TextRenderInfo textRenderInfo)
    {
        // Clone GraphicState to make further to be stored
        cloneTextRenderInfoField(textRenderInfo);

        LineSegment segment = textRenderInfo.getBaseline();

        // Get start and end point of text
        Vector start = segment.getStartPoint();
        Vector end   = segment.getEndPoint();

        String text = textRenderInfo.getText();
        isFirstRender = result.length() == 0;

        if (text.length() == 0 && text.trim().length() != 0)
        {
            startLine = start;
            return;
        }
        boolean isNewBlock = false;

        if (!isFirstRender)
        {
            Vector x1 = this.lastStart;
            Vector x2 = this.endLine;

            // Compute distance between current and last text
            float dist = x2.subtract(x1).cross(x1.subtract(start)).lengthSquared()
                    / x2.subtract(x1).lengthSquared();

            if (dist > textBlockIdentifier.sameLineThreshold)
            {
                // It's a new line. This text is not in the same block as the last one.
                isNewBlock = true;
            }
            else if (!letterIsBetween(startLine, endLine, start))
            {
                // If letter is between start and end of the sentence
                float spaceCharacterWidth = textRenderInfo.getSingleSpaceWidth();
                float spacing             = endLine.subtract(start).length();


                // If letters are too far
                if (spacing > spaceCharacterWidth / textBlockIdentifier.sameBlockFactorX)
                {
                    // Letters are in two different blocks
                    isNewBlock = true;
                }
                // Else if letters are in the same block, but too far to be attached
                else if (spacing > spaceCharacterWidth / textBlockIdentifier.spaceBlockFactorX)
                {
                    // Letters are separated with a space character
                    this.appendTextChunk(" ");
                }
            }
        }
        else
        {
            isFirstRender = false;

            if (startLine == null)
                startLine = start;
        }

        // If it's a new block
        if (isNewBlock)
        {
            // Create a new block from stored text
            push();
            // Save actual text position start as new block startLine
            startLine = start;
        }

        result.append(text);

        // Store current top and low position
        this.lastAscent = textRenderInfo.getAscentLine().getStartPoint();
        this.lastDescent = textRenderInfo.getDescentLine().getEndPoint();

        // Store start and end of text
        this.lastStart = start;
        this.endLine = end;

        // Keep text info to create future block
        blockTextInfos.add(textRenderInfo);
    }

    private void cloneTextRenderInfoField(TextRenderInfo textRenderInfo)
    {
        Class textRenderInfoClass = textRenderInfo.getClass();
        try
        {
            // Replace Graphics State
            Field gsField = getField(textRenderInfoClass, "gs", true);
            Object gs = gsField.get(textRenderInfo);
            if (gs != null) {
                GraphicsState sourceGS = (GraphicsState) gsField.get(textRenderInfo);
                GraphicsState outGS = new GraphicsState(sourceGS);
                gsField.set(textRenderInfo, outGS);
            }

            // Replace matrix userSpaceTransform
            Field textToUserSpaceTransformMatrixField = getField(textRenderInfoClass, "textToUserSpaceTransformMatrix", true);
            Object textToUserMatrix = textToUserSpaceTransformMatrixField.get(textRenderInfo);
            if (textToUserMatrix != null) {
                Matrix sourceMatrix = (Matrix) textToUserSpaceTransformMatrixField.get(textRenderInfo);
                Matrix outMatrix = new Matrix(sourceMatrix.get(0), // a
                        sourceMatrix.get(1), // b
                        sourceMatrix.get(3), // c
                        sourceMatrix.get(4), // d
                        sourceMatrix.get(6), // e
                        sourceMatrix.get(7)); // f
                textToUserSpaceTransformMatrixField.set(textRenderInfo, outMatrix);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private boolean letterIsBetween(Vector startLine, Vector endLine, Vector start)
    {
        Direction blockDirection = determineBlockDirection(startLine, endLine, lastAscent, lastDescent);

        int laneDirection = blockDirection.getLaneDirectionVector();
        boolean test = start.get(laneDirection) > startLine.get(laneDirection) && start.get(laneDirection) < endLine.get
                (laneDirection);

        return start.get(laneDirection) > startLine.get(laneDirection) && start.get(laneDirection) < endLine.get
            (laneDirection);
    }

    public void push()
    {
        if (blockTextInfos.size() <= 0)
            return;

        // Get xMin,Max yMin,Max
        double xMin, xMax, yMin, yMax;

        // If startBlockX < endBlockX
        if (startLine.get(0) < endLine.get(0))
        {
            xMin = startLine.get(0);
            xMax = endLine.get(0);
        }
        else
        {
            xMin = endLine.get(0);
            xMax = startLine.get(0);
        }

        // If startBlockY < endBlockY
        if (startLine.get(1) < endLine.get(1))
        {
            yMin = startLine.get(1);
            yMax = endLine.get(1);
        }
        else
        {
            yMin = endLine.get(1);
            yMax = startLine.get(1);
        }

        Block block = createBlock(xMin, xMax, yMin, yMax);

        extractedBlocks.add(block);

        // Start a new block
        isFirstRender = true;

        // Clear stored info
        blockTextInfos.clear();
        result.setLength(0);
    }

    private Block createBlock(double xMin, double xMax, double yMin, double yMax)
    {
        // Determine direction from line of block
        // lastAscent and lastDescent are last character rect top center side point and bottom center point
        Direction blockDirection = determineBlockDirection(startLine, endLine, lastAscent, lastDescent);
        Direction textDirection = determineTextDirection(lastAscent, lastDescent);

        Rectangle2 blockRectangle = createBlockRectangle(xMin, xMax, yMin, yMax, blockDirection);

        // Add color and font info
        Set<BaseColor>    fontColors = new HashSet<>();
        Set<BaseColor>    backColors = new HashSet<>();
        Set<DocumentFont> fonts      = new HashSet<>();
        for (TextRenderInfo render : blockTextInfos)
        {
            fontColors.add(render.getStrokeColor());
            backColors.add(render.getFillColor());
            fonts.add(render.getFont());
        }

        // Save block direction
        registerBlockDirection(blockDirection);

        // Create and return block with direction, rectangle and info
        return new Block(result.toString().trim(), blockRectangle, blockDirection, textDirection, fontColors, backColors,
                         fonts);
    }

    /**
     * Determine the text direction from one upper and lower rect position of one letter.
     * @param lastAscent upper rect position.
     * @param lastDescent lower rect position.
     * @return text direction.
     */
    private Direction determineTextDirection(Vector lastAscent, Vector lastDescent)
    {
        // X and Y axis are inverted
        // If text is on Y axis
        if (isAlongY(lastAscent.get(1), lastDescent.get(1)))
        {
            // If the text is not inverted along Y axis
            if (lastAscent.get(1) < lastDescent.get(1))
                return Direction.LEFT;
            else
                return Direction.RIGHT;
        }
        else
        {
            // If the text is not inverted along X axis
            if (lastAscent.get(0) < lastDescent.get(0))
                return Direction.TOP;
            else
                return Direction.BOTTOM;
        }
    }

    /**
     * Save block direction to estimate pdf orientation
     * @param direction new block direction
     */
    private void registerBlockDirection(Direction direction)
    {
        switch (direction)
        {
            case LEFT:
                nBlockFacingTop++;
                break;
            case RIGHT:
                nBlockFacingBottom++;
                break;
            case TOP:
                nBlockFacingLeft++;
                break;
            case BOTTOM:
                nBlockFacingRight++;
                break;
        }
    }

    /**
     * Determine block direction using line coordinates min max.
     *
     * @param startLine   start of the block middle line
     * @param lastEnd     end of the block middle line
     * @param lastAscent  one character's up side center
     * @param lastDescent one character's down side center
     *
     * @return block's direction
     */
    private Direction determineBlockDirection(Vector startLine,
                                              Vector lastEnd,
                                              Vector lastAscent,
                                              Vector lastDescent)
    {
        // X and Y axis are inverted
        if (isAlongY(startLine.get(1), lastEnd.get(1)))
        {
            // If Up point is before Down point along y axis
            if (lastAscent.get(1) < lastDescent.get(1))
                return Direction.LEFT;
            else
                return Direction.RIGHT;
        }
        else
        {
            // If Left point is before Right point along y axis
            if (startLine.get(1) < lastEnd.get(1))
                return Direction.TOP;
            else
                return Direction.BOTTOM;
        }
    }

    private boolean isAlongY(double y0, double y1)
    {
        return Math.abs(y0 - y1) < textBlockIdentifier.thresholdAlongY;
    }

    private Rectangle2 createBlockRectangle(double xMin, double xMax, double yMin, double yMax, Direction direction)
    {
        double minMaxOfAllText[] = getMinMaxOfAllText(direction);

        double startPointX;
        double startPointY;
        double blockHeight;
        double blockWidth;

        if (direction.equals(Direction.LEFT) || direction.equals(Direction.RIGHT))
        {
            startPointX = xMin;
            blockWidth = xMax - xMin;

            startPointY = minMaxOfAllText[0];
            blockHeight = (minMaxOfAllText[1] - minMaxOfAllText[0]);

            //double height = Math.abs(lastAscent.get(1) - lastDescent.get(1));
            //return new Rectangle2(xMin, yMin - height*0.5f, xMax - xMin, height);

        }
        else
        {
            startPointX = minMaxOfAllText[0];
            blockWidth = (minMaxOfAllText[1] - minMaxOfAllText[0]);

            startPointY = yMin;
            blockHeight = yMax - yMin;

            //double width = Math.abs(lastAscent.get(0) - lastDescent.get(0));
            //return new Rectangle2(xMin - width*0.5f, yMin,
                           // width, yMax - yMin);
        }

        return new Rectangle2(startPointX, startPointY, blockWidth, blockHeight);

    }

    /**
     * Get min and max along an axis
     *
     * @param direction of the computed min and max
     *
     * @return min and max, or null if there are no text
     */
    private double[] getMinMaxOfAllText(Direction direction)
    {
        double minMax[] = new double[2];

        // Get min and max for ascent and descent line
        switch (direction)
        {
            case LEFT:
                // Top point is before bottom point, on Y axis
                minMax[0] = getMinAscent(1);
                minMax[1] = getMaxDescent(1);
                break;

            case RIGHT:
                // Top point is after bottom point, on Y axis
                minMax[0] = getMinDescent(1);
                minMax[1] = getMaxAscent(1);
                break;

            case TOP:
                // Left point is before right point, on X axis
                minMax[0] = getMinAscent(0);
                minMax[1] = getMaxDescent(0);
                break;

            case BOTTOM:
                // Left point is after right point, on X axis
                minMax[0] = getMinDescent(0);
                minMax[1] = getMaxAscent(0);
                break;

            default:
                minMax[0] = -1;
                minMax[1] = -1;
        }

        return minMax;
    }

    private double getMinAscent(int axisIndex)
    {
        double minAscent = blockTextInfos.get(0).getAscentLine().getStartPoint().get(axisIndex);

        for (TextRenderInfo textRenderInfo : blockTextInfos)
        {
            double ascentY = textRenderInfo.getAscentLine().getStartPoint().get(axisIndex);
            if (ascentY < minAscent)
            {
                minAscent = ascentY;
            }
        }

        return minAscent;
    }

    private double getMaxAscent(int axisIndex)
    {
        double maxAscent = blockTextInfos.get(0).getAscentLine().getStartPoint().get(axisIndex);

        for (TextRenderInfo textRenderInfo : blockTextInfos)
        {
            double ascentY = textRenderInfo.getAscentLine().getStartPoint().get(axisIndex);
            if (ascentY > maxAscent)
            {
                maxAscent = ascentY;
            }
        }

        return maxAscent;
    }

    private double getMinDescent(int axisIndex)
    {
        double minDescent = blockTextInfos.get(0).getDescentLine().getStartPoint().get(axisIndex);

        for (TextRenderInfo textRenderInfo : blockTextInfos)
        {
            double ascentY = textRenderInfo.getDescentLine().getStartPoint().get(axisIndex);
            if (ascentY < minDescent)
            {
                minDescent = ascentY;
            }
        }

        return minDescent;
    }

    private double getMaxDescent(int axisIndex)
    {
        double maxDescent = blockTextInfos.get(0).getDescentLine().getStartPoint().get(axisIndex);

        for (TextRenderInfo textRenderInfo : blockTextInfos)
        {
            double ascentY = textRenderInfo.getDescentLine().getStartPoint().get(axisIndex);
            if (ascentY > maxDescent)
            {
                maxDescent = ascentY;
            }
        }

        return maxDescent;
    }


    public void endTextBlock()
    {

    }

    public void renderImage(ImageRenderInfo imageRenderInfo)
    {
        System.out.println("Image renderer not implemented");
    }

    public ArrayList<Block> getExtractedBlocks()
    {
        return extractedBlocks;
    }

    public ArrayList<Block> getExtractedBlocksAndRemovePdfOrientation(double pdfWidth, double pdfHeight)
    {
        finalizeWithOrientationTransform(pdfWidth, pdfHeight);

        return getExtractedBlocks();
    }

    public void finalizeWithOrientationTransform(double pdfWidth, double pdfHeight)
    {
        // Determine main direction of all blocks
        // If main direction along Y
        if (nBlockFacingTop + nBlockFacingBottom > nBlockFacingLeft + nBlockFacingRight)
        {
            /* if (nBlockFacingTop > nBlockFacingBottom)
             *      Facing top
             *      Don't need to change orientation
             */
            if (nBlockFacingTop <= nBlockFacingBottom)
            {
                // Facing bottom
                for (Block block: extractedBlocks)
                {
                    // Transform to top orientation
                    switch (block.getBlockOrientation())
                    {
                        case TOP:
                            block.setBlockOrientation(Direction.BOTTOM);
                            break;
                        case BOTTOM:
                            block.setBlockOrientation(Direction.TOP);
                            break;
                        case LEFT:
                            block.setBlockOrientation(Direction.RIGHT);
                            break;
                        case RIGHT:
                            block.setBlockOrientation(Direction.LEFT);
                            break;
                    }

                    Rectangle2 bound    = block.getBound();

                    // Transform rectangle, by mirroring on x Axis
                    Rectangle2 newBound = transformBottomTop(bound, pdfHeight);

                    block.setBound(newBound);
                }
            }
        }
        else
        {
            if (nBlockFacingLeft > nBlockFacingRight)
            {
                // Facing left
                for (Block block: extractedBlocks)
                {
                    // Transform direction
                    switch (block.getBlockOrientation())
                    {
                        case TOP:
                            block.setBlockOrientation(Direction.LEFT);
                            break;
                        case BOTTOM:
                            block.setBlockOrientation(Direction.RIGHT);
                            break;
                        case LEFT:
                            block.setBlockOrientation(Direction.BOTTOM);
                            break;
                        case RIGHT:
                            block.setBlockOrientation(Direction.TOP);
                            break;
                    }


                    Rectangle2 bound    = block.getBound();

                    // Rotate 90°
                    Rectangle2 newBound = transformLeftTop(bound);

                    block.setBound(newBound);
                }
            }
            else
            {
                // Facing right
                for (Block block: extractedBlocks)
                {
                    switch (block.getBlockOrientation())
                    {
                        case TOP:
                            block.setBlockOrientation(Direction.RIGHT);
                            break;
                        case BOTTOM:
                            block.setBlockOrientation(Direction.LEFT);
                            break;
                        case LEFT:
                            block.setBlockOrientation(Direction.TOP);
                            break;
                        case RIGHT:
                            block.setBlockOrientation(Direction.BOTTOM);
                            break;
                    }

                    Rectangle2 bound    = block.getBound();

                    // Rotate 90° + mirror on x
                    Rectangle2 newBound = transformRightTop(bound, pdfWidth, pdfHeight);

                    block.setBound(newBound);
                }
            }
        }
    }

    private static Rectangle2 transformBottomTop(Rectangle2 rect, double pdfHeight)
    {
        Rectangle2 newBound = rect.clone();

        // Mirror on X axis, and adding pdfHeight to make page in positives coordinates
        newBound.setY(-rect.getY() - rect.getHeight() + pdfHeight);

        return newBound;
    }

    private static Rectangle2 transformLeftTop(Rectangle2 rect)
    {
        Rectangle2 newBound = (Rectangle2) rect.clone();

        // Rotate 90°
        //noinspection SuspiciousNameCombination
        newBound.setX(rect.getY());
        //noinspection SuspiciousNameCombination
        newBound.setY(rect.getX());

        // Swap width and height
        //noinspection SuspiciousNameCombination
        newBound.setWidth(rect.getHeight());
        //noinspection SuspiciousNameCombination
        newBound.setHeight(rect.getWidth());

        return newBound;
    }

    private static Rectangle2 transformRightTop(Rectangle2 rect, double pdfWidth, double pdfHeight)
    {
        Rectangle2 newBound = (Rectangle2) rect.clone();

        // Swap x and y and apply inversion
        //noinspection SuspiciousNameCombination
        newBound.setX(-rect.getY() + pdfHeight - rect.getHeight());
        //noinspection SuspiciousNameCombination
        newBound.setY(-rect.getX() + pdfWidth - rect.getWidth());

        // Swap width and height
        //noinspection SuspiciousNameCombination
        newBound.setWidth(rect.getHeight());
        //noinspection SuspiciousNameCombination
        newBound.setHeight(rect.getWidth());

        return newBound;
    }
}
