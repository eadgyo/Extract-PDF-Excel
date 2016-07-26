package org.cora.extract_pdf_excel;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.cora.extract_pdf_excel.data.*;
import org.cora.extract_pdf_excel.data.array.My2DArray;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.lane.Lanes;
import org.cora.extract_pdf_excel.exception.IncorrectFileTypeException;
import org.cora.extract_pdf_excel.models.TextBlockIdentifier;
import org.cora.extract_pdf_excel.process.arrangement.BlockSorter;
import org.cora.extract_pdf_excel.process.extraction.PdfParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Extract data from pdf and convert it to excel page
 *
 * <p>
 * Full conversion process is divided in 4 operations <br/>
 * 1-Extract extractedData from PDF <br/>
 * 2-Sort extractedData in columns and lines <br/>
 * 3-Create Excel page from Sorted Data <br/>
 * 4-Add xcl Page to your excel workBook.
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
     * @param axisIndex     axis of lane, 0 for Line and 1 for Column
     * @param oppositeIndex opposite axis of lane, 1 for Line and 0 for Column
     *
     * @return Sorted Data from extracted pages. Data in extractedData are sorted in the right column and line. It
     * keeps page separation. Columns and lines contained sorted blocks according to Y-axis for columns and X-axis
     * for lines.
     */
    public static SortedData sortTransformedData(ExtractedData extractedData, int axisIndex, int oppositeIndex)
    {
        // Grouping all sortedPage
        SortedData sortedData = new SortedData();

        // For each extractedPage
        // Start at one
        for (int i = 1; i <= extractedData.numberOfPages(); i++)
        {
            ExtractedPage extractedPage = extractedData.getExtractedPage(i);

            // If page has been extracted
            if (extractedPage != null)
            {
                // Start creating sortedPage data
                Lanes      columns    = new Lanes();
                Lanes      lines      = new Lanes();
                SortedPage sortedPage = new SortedPage(columns, lines);

                Collection<Block> blocks = extractedPage.getBlocks();

                // Sort each block
                for (Block block : blocks)
                {
                    // Insert in the correct line or create new one
                    BlockSorter.insertInLanes(SortedPage.DEFAULT_LINE_AXIS,
                                              SortedPage.DEFAULT_OPPOSITE_LINE_AXIS,
                                              block,
                                              lines);

                    // Insert in the correct column or create new one
                    BlockSorter.insertInLanes(SortedPage.DEFAULT_COLUMN_AXIS,
                                              SortedPage.DEFAULT_OPPOSITE_COLUMN_AXIS,
                                              block,
                                              columns);
                }

                // Link sortedPage to his extractedPage
                sortedPage.setLinkExtractedPage(extractedPage);

                // Add sortedPage to sortedData
                sortedData.insertPage(i, sortedPage);
            }
        }

        return sortedData;
    }

    /**
     * Create excel page from sorted data, using 2D array created,
     *
     * @param sortedData data sorted in column and line
     *
     * @return list of excel pages.
     */
    public static ArrayList<XclPage> createExcelPages(SortedData sortedData)
    {
        ArrayList<XclPage> xclPages = new ArrayList<>();

        for (int pageIndex = 0; pageIndex < sortedData.numberOfPages(); pageIndex++)
        {
            // Get sortedPage
            SortedPage sortedPage = sortedData.getSortedPage(pageIndex);

            // If page exists (has been loaded)
            if (sortedPage != null)
            {
                // Create 2D array containing blocks using sorted lines and columns from sortedData
                My2DArray<Block>  arrayOfBlocks = sortedPage.create2DArrayOfBlocks();
                ArrayList<Double> linesSize     = sortedPage.getLinesHeight();
                ArrayList<Double> columnsSize   = sortedPage.getColumnsWidth();

                xclPages.add(new XclPage(arrayOfBlocks, linesSize, columnsSize));
            }
        }

        // return created Excel page
        return xclPages;
    }

    /**
     * Create excel sheet using workbook and xclPage, filling box with formatted text.
     *
     * @param sheetName name of the created excel sheet.
     * @param wb        used excel workBook to insert page
     * @param xclPage   excel page information. Containing cells and dimensions of columns and lines.
     *
     * @return created excel sheet.
     */
    public static HSSFSheet createExcelSheet(String sheetName, HSSFWorkbook wb, XclPage xclPage)
    {
        // Create excel sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // Parse columns and lines
        for (int line = 0; line < xclPage.numberOfLines(); line++)
        {
            HSSFRow createdLine = sheet.createRow(line);
            createdLine.setHeight((short) xclPage.getLineHeight(line));
            for (int col = 0; col < xclPage.numberOfColumns(); col++)
            {
                Block block = xclPage.getBlockAt(col, line);

                // If there is a block a the given position
                if (block != null)
                {
                    // Create a new cell and add the block content in this new cell
                    HSSFCell createdCell = createdLine.createCell(col);
                    createdCell.setCellValue(block.getFormattedText());
                }
            }
        }

        // Set the width of each lane
        for (int col = 0; col < xclPage.numberOfColumns(); col++)
        {
            sheet.setColumnWidth(col, (int) xclPage.getColumnWidth(col));
        }

        return null;
    }
}
