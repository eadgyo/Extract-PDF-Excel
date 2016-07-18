package org.cora.extract_pdf_excel.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eadgyo on 12/07/16.
 */

/**
 * Blocks sorted in lines and columns, and separated in pages
 */
public class SortedData
{
    private Map<Integer, SortedPage> sortedPages;

    public SortedData()
    {
        sortedPages = new HashMap<Integer, SortedPage>();
    }

    /**
     * Insert an sortedPage
     *
     * @param pageIndex   index of concerned page
     * @param sortedPage inserted sortedPage
     */
    public void insertPage(int pageIndex, SortedPage sortedPage)
    {
        this.sortedPages.put(pageIndex, sortedPage);
    }

    /**
     * Get all blocks in all page
     *
     * @return all blocks separated in page
     */
    public Map<Integer, SortedPage> getPages()
    {
        return sortedPages;
    }

    /**
     * Get extracted page using his page index
     *
     * @param pageIndex index of page
     * @return page with extracted data
     */
    public SortedPage getSortedPage(int pageIndex)
    {
        return sortedPages.get(pageIndex);
    }

    public int numberOfPages()
    {
        return sortedPages.size();
    }
}
