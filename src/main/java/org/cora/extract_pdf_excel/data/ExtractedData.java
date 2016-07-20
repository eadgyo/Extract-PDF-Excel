package org.cora.extract_pdf_excel.data;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.exception.NoPageAtIndexException;

import java.util.*;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Holds extracted data separated in pages.
 */
public class ExtractedData
{
    /**
     * ExtractedPage link to his pageIndex
     */
    private Map<Integer, ExtractedPage> extractedPages;


    public ExtractedData()
    {
        this.extractedPages = new HashMap<Integer, ExtractedPage>();
    }

    /**
     * Insert an extractedPage
     *
     * @param pageIndex   index of concerned page
     * @param extractedPage inserted extractedPage
     */
    public void insertPage(int pageIndex, ExtractedPage extractedPage)
    {
        this.extractedPages.put(pageIndex, extractedPage);
    }

    /**
     * Get all blocks in all page
     *
     * @return all blocks separated in page
     */
    public Map<Integer, ExtractedPage> getPages()
    {
        return extractedPages;
    }

    /**
     * Get blocks in on page
     *
     * @param pageIndex index of concerned page
     *
     * @return all block in defined page, or null if page does not exist
     */
    public ArrayList<Block> getBlocksInPage(int pageIndex)
    {
        return extractedPages.get(pageIndex).getBlocks();
    }

    /**
     * Get page containing blocks, and her lengths, throw <tt>NoPageAtIndexException</tt> if page does not exist
     *
     * @param pageIndex index of concerned page
     *
     * @return page or null if it does not exist
     */
    private ExtractedPage getPageWithException(int pageIndex)
    {
        ExtractedPage extractedPage = getExtractedPage(pageIndex);

        try
        {
            if (extractedPage != null)
            {
                return extractedPage;
            }
            else
            {
                throw new NoPageAtIndexException(pageIndex);
            }
        }
        catch (NoPageAtIndexException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get extracted page using his page index
     *
     * @param pageIndex index of page
     * @return page with extracted data
     */
    public ExtractedPage getExtractedPage(int pageIndex)
    {
        return extractedPages.get(pageIndex);
    }

    public int numberOfPages()
    {
        return extractedPages.size();
    }

    public int numberOfBlocksInPage(int pageIndex)
    {
        ExtractedPage extractedPage = getPageWithException(pageIndex);
        return (extractedPage != null) ? extractedPage.numberOfBlocks() : 0;
    }

    public float getPageWidth(int pageIndex)
    {
        ExtractedPage extractedPage = getPageWithException(pageIndex);
        return (extractedPage != null) ? extractedPage.getWidth() : 0;
    }

    public float getPageHeight(int pageIndex)
    {
        ExtractedPage extractedPage = getPageWithException(pageIndex);
        return (extractedPage != null) ? extractedPage.getHeight() : 0;
    }
}
