package org.eadge.extractpdfexcel;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eadge.extractpdfexcel.data.*;
import org.eadge.extractpdfexcel.data.array.My2DArray;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.lane.Lanes;
import org.eadge.extractpdfexcel.debug.display.FrameCreator;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;
import org.eadge.extractpdfexcel.process.arrangement.BlockSorter;
import org.eadge.extractpdfexcel.process.extraction.PdfParser;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
     *
     * Using the default text block identifier
     * </p>
     *
     * @param path                pdf file location
     *
     * @return extractedData separate in blocks containing text and position for each page.
     */
    public static ExtractedData extractFromFile(String path) throws
                                                                                         FileNotFoundException,
                                                                                         IncorrectFileTypeException
    {
        return extractFromFile(path, new TextBlockIdentifier());
    }

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

            if (textBlockIdentifier.cleanDuplicated)
                parser.cleanDuplicatedData();
            if (textBlockIdentifier.mergeFactor > 1.0)
                parser.mergeBlocks(textBlockIdentifier.mergeFactor);

            parser.close();
            // return extractedData extracted with parser
            return parser.getExtractedData();
        }
        else
        {
            throw new FileNotFoundException(path);
        }
    }

    /**
     * Remove duplicated blocks
     * @param extractedData modified collection of blocks
     */
    public static void removeDuplicatedData(ExtractedData extractedData)
    {
        extractedData.cleanDuplicatedData();
    }

    /**
     * Merge near blocks following fonts and orientation rules
     * @param extractedData modified colleciton of blocks
     */
    public static void mergeData(ExtractedData extractedData, double mergeFactor)
    {
        extractedData.mergeBlocks(mergeFactor);
    }

    /**
     * Remove duplicated data
     * Merge near blocks following fonts and orientation rules
     * @param extractedData modified colleciton of blocks
     */
    public static void cleanAndMergeData(ExtractedData extractedData, double mergeFactor)
    {
        extractedData.cleanDuplicatedData();
        extractedData.mergeBlocks(mergeFactor);
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
     * @param extractedData extracted pages from one pdf file
     * //@param reinsertBlockMoreCollidingHigherLane if true, at the end of insert process, move block to higher lane,
     *                                             if colliding percent between block and higher lane is higher than
     *                                             block and actual block lane.
     *
     * @return Sorted Data from extracted pages. Data in extractedData are sorted in the right column and line. It
     * keeps page separation. Columns and lines contained sorted blocks according to Y-axis for columns and X-axis
     * for lines.
     */
    public static SortedData sortExtractedData(ExtractedData extractedData)
    {
        return sortExtractedData(extractedData, SortedPage.DEFAULT_LINE_AXIS, SortedPage.DEFAULT_COLUMN_AXIS);
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
     * @param extractedData extracted pages from one pdf file
     * @param axisIndex     axis of lane, 0 for Line and 1 for Column
     * @param oppositeIndex opposite axis of lane, 1 for Line and 0 for Column
     * //@param reinsertBlockMoreCollidingHigherLane if true, at the end of insert process, move block to higher lane,
     *                                             if colliding percent between block and higher lane is higher than
     *                                             block and actual block lane.
     *
     * @return Sorted Data from extracted pages. Data in extractedData are sorted in the right column and line. It
     * keeps page separation. Columns and lines contained sorted blocks according to Y-axis for columns and X-axis
     * for lines.
     */
    public static SortedData sortExtractedData(ExtractedData extractedData, int axisIndex, int oppositeIndex)
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
                // Create sortedPage
                SortedPage sortedPage = sortExtractedPage(extractedPage, axisIndex, oppositeIndex);

                // Add sortedPage to sortedData
                sortedData.insertPage(i, sortedPage);
            }
        }

        return sortedData;
    }

    /**
     * Sort extractedPage in both columns and lines.
     *
     * <p>
     * Each block in extractedPage are processed one by one. Block is first added to the right column, then in
     * the right line.
     * </p>
     *
     * <p>
     * axisIndex and oppositeIndex are used to merge Line and Column process.
     * </p>
     *
     * @param extractedPage              extracted page from one pdf file
     * @param axisIndex                  axis of lane, 0 for Line and 1 for Column
     * @param oppositeIndex              opposite axis of lane, 1 for Line and 0 for Column
     * //@param reinsertBlockMoreCollidingHigherLane if true, at the end of insert process, move block to higher lane,
     *                                             if colliding percent between block and higher lane is higher than
     *                                             block and actual block lane.
     *
     * @return Sorted Data from extracted pages. Data in extractedData are sorted in the right column and line. It
     * keeps page separation. Columns and lines contained sorted blocks according to Y-axis for columns and X-axis
     * for lines.
     */
    public static SortedPage sortExtractedPage(ExtractedPage extractedPage,
                                               int axisIndex,
                                               int oppositeIndex)
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
            BlockSorter.insertInLanes(axisIndex,
                                      oppositeIndex,
                                      block,
                                      lines);

            // Insert in the correct column or create new one
            BlockSorter.insertInLanes(oppositeIndex,
                                      axisIndex,
                                      block,
                                      columns);
        }

        // If end reinserting block option is activated
/*        if (reinsertBlockMoreCollidingHigherLane)
        {
            BlockSorter.reinsertBlockMoreCollidingHigherLane(axisIndex,
                                                             oppositeIndex,
                                                   lines);
            BlockSorter.reinsertBlockMoreCollidingHigherLane(oppositeIndex, axisIndex,
                                      columns);
        }*/

        // Link sortedPage to his extractedPage
        sortedPage.setLinkExtractedPage(extractedPage);

        return sortedPage;
    }

    /**
     * Create excel pages from sorted data, using 2D array created,
     *
     * @param sortedData data sorted in column and line
     *
     * @return list of excel pages.
     */
    public static ArrayList<XclPage> createExcelPages(SortedData sortedData)
    {
        ArrayList<XclPage> xclPages = new ArrayList<>();

        for (int pageIndex = 1; pageIndex <= sortedData.numberOfPages(); pageIndex++)
        {
            // Get sortedPage
            SortedPage sortedPage = sortedData.getSortedPage(pageIndex);

            // If page exists (has been loaded)
            if (sortedPage != null)
            {
                xclPages.add(createExcelPage(sortedPage));
            }
        }

        // return created Excel page
        return xclPages;
    }

    /**
     * Create excel page from sorted data, using 2D array created,
     *
     * @param sortedPage data sorted in column and line
     *
     * @return list of excel pages.
     */
    public static XclPage createExcelPage(SortedPage sortedPage)
    {
        // Create 2D array containing blocks using sorted lines and columns from sortedData
        My2DArray<Block>  arrayOfBlocks = sortedPage.create2DArrayOfBlocks();
        ArrayList<Double> columnsSize   = sortedPage.getColumnsWidth();
        ArrayList<Double> linesSize     = sortedPage.getLinesHeight();

        // return created Excel page
        return new XclPage(arrayOfBlocks, columnsSize, linesSize);
    }

    /**
     * Create excel sheet using workbook and xclPage, filling box with formatted text.
     *
     * @param sheetName name of the created excel sheet.
     * @param wb        used excel workBook to insert page
     * @param xclPage   excel page information. Containing cells and dimensions of columns and lines.
     * @param lineFactor line size factor
     * @param columnFactor column size factor
     * @return created excel sheet.
     */
    public static HSSFSheet createExcelSheet(String sheetName, HSSFWorkbook wb, XclPage xclPage, double lineFactor, double
            columnFactor)
    {
        // Create excel sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // Parse columns and lines
        for (int line = 0; line < xclPage.numberOfLines(); line++)
        {
            HSSFRow createdLine = sheet.createRow(line);
            if (lineFactor != 0)
                createdLine.setHeight((short) (xclPage.getLineHeight(line) * 50));
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
        if (columnFactor != 0)
        {
            for (int col = 0; col < xclPage.numberOfColumns(); col++)
            {
                sheet.setColumnWidth(col, (int) xclPage.getColumnWidth(col) * 20);
            }
        }

        return null;
    }

    /**
     * Convert a pdf file into an excel sheets
     * @param sourcePDFPath path for the used source pdf
     * @param workbook used workBook for created sheets
     * @param textBlockIdentifier defines parameter used to
     * @param lineAxis 0 if the pdf is in portray mode
     * @param columnAxis 1 if the pdf is in portray mode
     * @param lineFactor line size factor
     * @param columnFactor column size factor
     */
    public static ArrayList<HSSFSheet> createExcelSheets(String sourcePDFPath,
                                       HSSFWorkbook workbook,
                                       TextBlockIdentifier textBlockIdentifier,
                                        int lineAxis,
                                        int columnAxis, double lineFactor,
                                                         double columnFactor) throws FileNotFoundException,
                                                                        IncorrectFileTypeException
    {
        ArrayList<HSSFSheet> sheets = new ArrayList<>();

        // Extract data from the source pdf file
        ExtractedData extractedData = PdfConverter.extractFromFile(sourcePDFPath, textBlockIdentifier);

        // Sort Data
        SortedData sortedData = PdfConverter.sortExtractedData(extractedData, lineAxis, columnAxis);

        // Create 2D array pages containing information
        ArrayList<XclPage> excelPages = PdfConverter.createExcelPages(sortedData);

        // Create sheets for each pages
        int page = 1;
        for (XclPage excelPage : excelPages)
        {
            HSSFSheet excelSheet = PdfConverter.createExcelSheet("page " + page, workbook, excelPage, lineFactor, columnFactor);
            sheets.add(excelSheet);
            page++;
        }

        return sheets;
    }

    /**
     * Convert a pdf file into an excel sheets
     * @param sourcePDFPath path for the used source pdf
     * @param workbook used workbook for creating sheets
     */
    public static ArrayList<HSSFSheet> createExcelSheets(String sourcePDFPath, HSSFWorkbook workbook) throws
                                                                                           FileNotFoundException,
                                                                                       IncorrectFileTypeException
    {
        return createExcelSheets(sourcePDFPath, workbook, new TextBlockIdentifier(), 0, 1, 0, 0);
    }

    /**
     * Create Excel file from pdf source
     * @param sourcePDFPath path for the used source pdf
     * @param xclPath path for the created excel file
     * @param textBlockIdentifier defines parameter used to
     * @param lineAxis 0 if the pdf is in portray mode
     * @param columnAxis 1 if the pdf is in portray mode
     * @param lineFactor line size factor
     * @param columnFactor column size factor
     */
    public static void createExcelFile(String sourcePDFPath, String xclPath,
                                       TextBlockIdentifier textBlockIdentifier,
                                       int lineAxis,
                                       int columnAxis,
                                       double lineFactor,
                                       double columnFactor) throws IOException
    {
        HSSFWorkbook workbook = new HSSFWorkbook();

        ArrayList<HSSFSheet> excelSheets = createExcelSheets(sourcePDFPath,
                                                             workbook,
                                                             textBlockIdentifier,
                                                             lineAxis,
                                                             columnAxis,
                                                             lineFactor,
                                                             columnFactor);

        FileOutputStream out = new FileOutputStream(xclPath);
        workbook.write(out);
        out.close();
    }

    /**
     * Create Excel file from pdf source
     * @param sourcePDFPath path for the used source pdf
     * @param xclPath path for the created excel file
     */
    public static void createExcelFile(String sourcePDFPath, String xclPath) throws IOException
    {
        createExcelFile(sourcePDFPath, xclPath, new TextBlockIdentifier(),0, 1,0, 0);
    }

    /**
     * Display the xclPage
     * @param xclPage displayed xclPage
     */
    public static void displayXCLPage(XclPage xclPage)
    {
        FrameCreator.displayXclPage("Xcl", 800, 600, xclPage);
    }

    /**
     * Display the xclPages
     * @param xclPages displayed xclPages
     */
    public static Collection<JFrame>  displayXCLPages(Collection<XclPage> xclPages)
    {
        ArrayList<JFrame> frames = new ArrayList<>();

        FrameCreator.displayXclPages("XCL", 800, 600, xclPages);

        return frames;
    }

    /**
     * Display the xclPage
     * @param sourcePdf displayed pdf converted to xcl
     */
    public static Collection<JFrame> displayXCLPage(String sourcePdf) throws FileNotFoundException, IncorrectFileTypeException
    {
        ArrayList<XclPage> xclPages = convertFileToXclPages(sourcePdf);
        Collection<JFrame> frames = displayXCLPages(xclPages);

        return frames;
    }

    public static ArrayList<XclPage> convertFileToXclPages(String sourcePdf) throws FileNotFoundException,
                                                                           IncorrectFileTypeException
    {
        // Extract data from the source pdf file
        ExtractedData extractedData = PdfConverter.extractFromFile(sourcePdf, new TextBlockIdentifier());

        // Sort Data
        SortedData sortedData = PdfConverter.sortExtractedData(extractedData, 0, 1);

        // Create 2D array pages containing information
        return PdfConverter.createExcelPages(sortedData);
    }
}
