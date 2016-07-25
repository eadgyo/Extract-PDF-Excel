package org.cora.extract_pdf_excel.data;

import org.cora.extract_pdf_excel.data.array.My2DArray;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Lanes;

import java.util.ArrayList;

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

        // Fill the array
        for (int col = 0; col < columns.size(); col++)
        {
            /*for (int )

            // Get his line index
            int line = col

            my2DArray.set*/
        }
        return null;
    }

    /**
     * Get the bounds of each line along the opposite axis of the lane.
     *
     * @return bounds of each line lane. The first key is the start of the first lane, the second the end of the first
     * and the start of the second lane etc...
     */
    public ArrayList<Double> getLinesBounds()
    {
        return lines.getLanesBounds(DEFAULT_OPPOSITE_LINE_AXIS);
    }

    /**
     * Get the end of each column along the opposite axis of the lane.
     *
     * @return End of each column lane. The first key is the start of the first lane, the second the end of the first
     * and the start of the second lane etc...
     */
    public ArrayList<Double> getColumnsStart()
    {
        return columns.getLanesBounds(DEFAULT_OPPOSITE_COLUMN_AXIS);
    }
}
