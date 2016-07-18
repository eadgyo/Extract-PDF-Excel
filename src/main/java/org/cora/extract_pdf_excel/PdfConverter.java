package org.cora.extract_pdf_excel;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.cora.extract_pdf_excel.data.ExtractedData;
import org.cora.extract_pdf_excel.data.ExtractedPage;
import org.cora.extract_pdf_excel.data.SortedData;
import org.cora.extract_pdf_excel.data.SortedPage;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Lanes;
import org.cora.extract_pdf_excel.exception.IncorrectFileTypeException;
import org.cora.extract_pdf_excel.process.extraction.PdfParser;
import org.cora.extract_pdf_excel.models.TextBlockIdentifier;
import org.cora.extract_pdf_excel.process.arrangement.BlockSorter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by eadgyo on 12/07/16.
 */

/**
 * Extract data from pdf and convert it to excel page
 *
 * <p>
 * Full conversion process is divided in 3 operations <br/>
 * 1-Extract transformedData from PDF <br/>
 * 2-Arrange transformedData in colums and lines <br/>
 * 3-Create Excel page from Arranged Data <br/>
 * </p>
 *
 * <p>
 * Operations must be done in designated order.
 * </p>
 */
public class PdfConverter
{

    /**
     * Extract transformedData from PDF
     * <p>
     * Read pdf file from path location. If file exists and is in pdf format, extract all text from all pages in pdf
     * file. If file exists but is not a pdf throw <tt>IncorrectFileException</tt>. If file doesn't exist or is not
     * readable throw <tt> NoSuchFileException</tt>.
     * </p>
     *
     * @param path                pdf file location
     * @param textBlockIdentifier TextBlockIdentifier define text separation parameters (space between to letters to
     *                            create a space, height between to letters creating new line).
     *
     * @return extractedData separate in blocks containing text and position for each page.
     */
    public static ExtractedData extractFromFile(String path,
                                                TextBlockIdentifier textBlockIdentifier) throws
                                                                                         FileNotFoundException,
                                                                                         IncorrectFileTypeException
    {
        // Create file reader
        File file = new File(path);

        if (file.exists())
        {
            // Create Pdf Extractor using path location
            PdfReader pdf = null;
            try
            {
                pdf = new PdfReader(path);
            }
            catch (IOException e)
            {
                // pdf is not readable
                throw new IncorrectFileTypeException(path);
            }

            // Pdf is readable
            // Create parser to extract data from pdf
            PdfParser parser = new PdfParser(pdf, textBlockIdentifier);

            // Extract all data
            parser.readAllPage();

            // return extractedData extracted with parser
            return parser.getExtractedData();
        }
        else
        {
            throw new FileNotFoundException(path);
        }
    }

    /**
     * Sort extractedData in both columns and lines.
     *
     * <p>
     * Each block in extractedData are processed one by one. Block is first added to the right column, then in
     * the right line.
     * </p>
     *
     * <p>
     * axisIndex and oppositeIndex are used to merge Line and Column process.
     * </p>
     *
     * @param extractedData extracted extractedData from extractFromFile process
     * @param axisIndex axis of lane, 0 for Line and 1 for Column
     * @param oppositeIndex opposite axis of lane, 1 for Line and 0 for Column
     *
     * @return Sorted Data. Data in extractedData are sorted in the right column and line. It keeps page separation.
     * Columns and lines contained sorted blocks according to Y-axis for columns and X-axis for lines.
     */
    public static SortedData sortTransformedData(ExtractedData extractedData, int axisIndex, int oppositeIndex)
    {
        // For each extractedPage
        // Start at one
        for (int i = 1; i <= extractedData.numberOfPages(); i++)
        {
            ExtractedPage extractedPage = extractedData.getExtractedPage(i);

            // If page has been extracted
            if (extractedPage != null)
            {
                // Start creating sortedPage
                Lanes columns = new Lanes();
                Lanes lines   = new Lanes();

                ArrayList<Block> blocks = extractedPage.getBlocks();

                // Sort each block
                for (int j = 0; j < blocks.size(); j++)
                {
                    Block block =  blocks.get(j);
                    BlockSorter.insertInLanes(block, columns);
                    BlockSorter.insertInLanes(block, lines);
                }

                // End sortedPage creation
                SortedPage sortedPage = new SortedPage();

            }
        }


        // Get lowerLane from block

        // If lowerLane exists AND lowerLane is colliding
            // Compare block to existing in lane blocks
            // If there are one block colliding along opposite axis
                // Take the inserted block or existing block, with higher coordinate
                // If another lane is colliding with higherBlock
                    // Save this lane to reinsert blocks
                    // Change position of this lane as the min of actualLane coordinate and higherBlock coordinate
                // Else
                    // Create a new lane and insert it in lanes
                    // Save lane to reinsert blocks

                // Save lower lane blocks
                // Clean lower lane
                // Clear width by setting lower lane length along axis to 0
                // Set lower lane length along opposite axis to min of actual length and difference of low coordinate
                // of savedLane and low coordinate of lowerLane to remove savedLane and lowerLane collision.

                // Reinsert all savedBlocks of lowerLane in his corresponding lane (savedLane or lowerLane)


        // Else the correct lane does no exist
            // Create a new lane with block rect
            // Insert block in created lane



        // Insert all blocks in sortedData, in the right column and the right line

        return null;
    }

    /**
     * Create excel page from sorted data, using 2D array created,
     *
     * @param sortedData data sorted in column and line
     *
     * @return excel sheet
     */
    public static HSSFSheet createExcelPage(SortedData sortedData)
    {
        // Create 2D array containing blocks using sorted lines and columns from sortedData

        // return created Excel page using 2D array

        return null;
    }

    /**
     * Create excel page from 2D array of blocks, filling box with formatted text.
     *
     * @param array2OfBlocks 2D array of blocks
     * @return created excel page using array2OfBlocks
     */
    public static HSSFSheet createExcelPage(Block array2OfBlocks[][])
    {
        // Fill Excel Sheet with 2D array
            // Insert text using formatted text

        return null;
    }
}
