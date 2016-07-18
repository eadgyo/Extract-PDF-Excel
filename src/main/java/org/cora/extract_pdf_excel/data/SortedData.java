package org.cora.extract_pdf_excel.data;

import org.cora.extract_pdf_excel.data.lane.Lanes;

/**
 * Created by Eadgyo on 12/07/16.
 */
public class SortedData
{
    private Lanes Columns;
    private Lanes Lines;

    public SortedData()
    {
        Columns = new Lanes();
        Lines = new Lanes();
    }

    public SortedData(Lanes columns, Lanes lines)
    {
        Columns = columns;
        Lines = lines;
    }

    public Lanes getColumns()
    {
        return Columns;
    }

    public void setColumns(Lanes columns)
    {
        Columns = columns;
    }

    public Lanes getLines()
    {
        return Lines;
    }

    public void setLines(Lanes lines)
    {
        Lines = lines;
    }


}
