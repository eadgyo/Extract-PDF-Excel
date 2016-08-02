package org.eadge.extractpdfexcel.data;

import org.eadge.extractpdfexcel.data.array.My2DArray;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.data.lane.Lanes;
import org.eadge.extractpdfexcel.exception.NoCorrespondingLane;

import java.util.*;

/**
 * Created by eadgyo on 18/07/16.
 * <p/>
 * Blocks sorted in columns and lines for one page
 */
public class SortedPage
{
    public final static int X_AXIS = 0;
    public final static int Y_AXIS = 1;

    public final static int DEFAULT_COLUMN_AXIS          = Y_AXIS;
    public final static int DEFAULT_OPPOSITE_COLUMN_AXIS = X_AXIS;
    public final static int DEFAULT_LINE_AXIS            = X_AXIS;
    public final static int DEFAULT_OPPOSITE_LINE_AXIS   = Y_AXIS;

    private Lanes columns;
    private Lanes lines;

    /**
     * Link to corresponding extractedPage
     */
    private ExtractedPage linkExtractedPage = null;

    public SortedPage()
    {
        columns = new Lanes();
        lines = new Lanes();
    }

    public SortedPage(Lanes columns, Lanes lines)
    {
        this.columns = columns;
        this.lines = lines;
    }

    /**
     * Get linked extracted page
     *
     * @return linked extracted page or null if link has not been set.
     */
    public ExtractedPage getLinkExtractedPage()
    {
        return linkExtractedPage;
    }

    /**
     * Link to corresponding extractedPage data can be added
     *
     * @param extractedPage corresponding extractedPage
     */
    public void setLinkExtractedPage(ExtractedPage extractedPage)
    {
        linkExtractedPage = extractedPage;
    }

    public Lanes getColumns()
    {
        return columns;
    }

    public void setColumns(Lanes columns)
    {
        this.columns = columns;
    }

    public Lanes getLines()
    {
        return lines;
    }

    public void setLines(Lanes lines)
    {
        this.lines = lines;
    }

    public int columnsSize()
    {
        return columns.size();
    }

    public int linesSize()
    {
        return lines.size();
    }

    /**
     * Create a 2D array of blocks using sorted lines and sorted columns
     */
    public My2DArray<Block> create2DArrayOfBlocks()
    {
        // Create array
        My2DArray<Block> my2DArray = new My2DArray<>();

        // Create each columns
        for (int col = 0; col < columns.size(); col++)
        {
            my2DArray.addColumn();
        }

        // Create each lines
        for (int line = 0; line < lines.size(); line++)
        {
            my2DArray.addLine();
        }

        // Get sorted columns
        Set<Map.Entry<Double, Lane>> sortedColumns = columns.getSortedLanes();

        // For each column, get all block, get the corresponding line. Insert the block at line and column location.
        int col = 0;
        for (Iterator<Map.Entry<Double, Lane>> iterator = sortedColumns.iterator(); iterator.hasNext(); col++)
        {
            Map.Entry<Double, Lane> sortedColumn = iterator.next();
            Lane                    column       = sortedColumn.getValue();

            // Get blocks in the column
            Collection<Block> blocks = column.getBlocksCollection();
            for (Block block : blocks)
            {
                // Search his line index
                int line = lines.getLaneIndexOfBlock(DEFAULT_OPPOSITE_LINE_AXIS, block);

                try
                {
                    if (line == -1)
                        throw new NoCorrespondingLane();

                    // Set at the position, the block
                    my2DArray.set(col, line, block);
                }
                catch (NoCorrespondingLane noCorrespondingLane)
                {
                    noCorrespondingLane.printStackTrace();
                }
            }
        }

        return my2DArray;
    }

    /**
     * Get the bounds of each line along the opposite axis of the lane.
     *
     * @return bounds of each line lane. The first key is the start of the first lane, the second the end of the first
     * and the start of the second lane etc...
     */
    public ArrayList<Double> getLinesHeight()
    {
        return lines.getLanesLength(DEFAULT_OPPOSITE_LINE_AXIS);
    }

    /**
     * Get the end of each column along the opposite axis of the lane.
     *
     * @return End of each column lane. The first key is the start of the first lane, the second the end of the first
     * and the start of the second lane etc...
     */
    public ArrayList<Double> getColumnsWidth()
    {
        return columns.getLanesLength(DEFAULT_OPPOSITE_COLUMN_AXIS);
    }
}
