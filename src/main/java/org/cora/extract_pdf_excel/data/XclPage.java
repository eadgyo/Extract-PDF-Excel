package org.cora.extract_pdf_excel.data;

import org.cora.extract_pdf_excel.data.array.My2DArray;
import org.cora.extract_pdf_excel.data.block.Block;

import java.util.ArrayList;

/**
 * Created by eadgyo on 25/07/16.
 *
 * Holds cells of one converted excel page and dimensions of columns and lines of the page.
 */
public class XclPage
{
    private My2DArray<Block>  cells;
    private ArrayList<Double> columnsWidth;
    private ArrayList<Double> linesHeight;

    /**
     * @param cells Array of cells, containing blocks
     * @param columnsWidth width of each column
     * @param linesHeight height of each line
     */
    public XclPage(My2DArray<Block> cells, ArrayList<Double> columnsWidth, ArrayList<Double> linesHeight)
    {
        this.cells = cells;
        this.columnsWidth = columnsWidth;
        this.linesHeight = linesHeight;
    }

    /**
     * @param cells Array of cells, containing blocks
     */
    public XclPage(My2DArray<Block> cells)
    {
        this.cells = cells;
        this.columnsWidth = new ArrayList<>();
        this.linesHeight = new ArrayList<>();
    }

    public XclPage()
    {
        this.cells = new My2DArray<>();
        this.columnsWidth = new ArrayList<>();
        this.linesHeight = new ArrayList<>();
    }

    public My2DArray<Block> getCells()
    {
        return cells;
    }

    public void setCells(My2DArray<Block> cells)
    {
        this.cells = cells;
    }

    public ArrayList<Double> getColumnWidth()
    {
        return columnsWidth;
    }

    public void setColumnsWidth(ArrayList<Double> columnsWidth)
    {
        this.columnsWidth = columnsWidth;
    }

    public ArrayList<Double> getLinesHeight()
    {
        return linesHeight;
    }

    public void setLinesHeight(ArrayList<Double> linesHeight)
    {
        this.linesHeight = linesHeight;
    }

    /**
     *
     * @return number of columns of the array cells
     */
    public int numberOfColumns()
    {
        return cells.numberOfColumns();
    }

    /**
     *
     * @return number of lines of the array cells
     */
    public int numberOfLines()
    {
        return cells.numberOfLines();
    }

    /**
     * Get a block at a given position
     * @param col column index
     * @param line line index
     * @return block or null if there is no block at this place.
     */
    public Block getBlockAt(int col, int line)
    {
        return cells.get(col, line);
    }

    /**
     * Get the height size of a line
     * @param line line index
     * @return height of the line
     */
    public double getLineHeight(int line)
    {
        assert (line >= 0 && line <= numberOfLines());

        return linesHeight.get(line);
    }

    /**
     * Get the width size of a column
     * @param column column index
     * @return width of the column
     */
    public double getColumnWidth(int column)
    {
        assert (column >= 0 && column <= numberOfColumns());

        return columnsWidth.get(column);
    }
}
