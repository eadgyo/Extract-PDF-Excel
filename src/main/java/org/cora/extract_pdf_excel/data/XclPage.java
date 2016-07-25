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
    private ArrayList<Double> colsSize;
    private ArrayList<Double> linesSize;

    public XclPage(My2DArray<Block> cells, ArrayList<Double> colsSize, ArrayList<Double> linesSize)
    {
        this.cells = cells;
        this.colsSize = colsSize;
        this.linesSize = linesSize;
    }

    public XclPage(My2DArray<Block> cells)
    {
        this.cells = cells;
        this.colsSize = new ArrayList<>();
        this.linesSize = new ArrayList<>();
    }

    public XclPage()
    {
        this.cells = new My2DArray<>();
        this.colsSize = new ArrayList<>();
        this.linesSize = new ArrayList<>();
    }

    public My2DArray<Block> getCells()
    {
        return cells;
    }

    public void setCells(My2DArray<Block> cells)
    {
        this.cells = cells;
    }

    public ArrayList<Double> getColsSize()
    {
        return colsSize;
    }

    public void setColsSize(ArrayList<Double> colsSize)
    {
        this.colsSize = colsSize;
    }

    public ArrayList<Double> getLinesSize()
    {
        return linesSize;
    }

    public void setLinesSize(ArrayList<Double> linesSize)
    {
        this.linesSize = linesSize;
    }


}
