package org.cora.extract_pdf_excel.data;

import org.cora.extract_pdf_excel.data.lane.Lanes;

/**
 * Created by eadgyo on 18/07/16.
 */

/**
 * Blocks sorted in columns and lines for one page
 */
public class SortedPage
{

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
     * Link to corresponding extractedPage data can be added
     *
     * @param extractedPage corresponding extractedPage
     */
    public void setLinkExtractedPage(ExtractedPage extractedPage)
    {
        linkExtractedPage = extractedPage;
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
}
