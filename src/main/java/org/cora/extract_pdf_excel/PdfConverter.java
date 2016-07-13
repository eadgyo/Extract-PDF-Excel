package org.cora.extract_pdf_excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.cora.extract_pdf_excel.data.SortedData;
import org.cora.extract_pdf_excel.data.TransformedData;
import org.cora.extract_pdf_excel.exception.IncorrectFileTypeException;
import org.cora.extract_pdf_excel.models.BlockMerger;
import org.cora.extract_pdf_excel.models.StringFormatter;
import org.cora.extract_pdf_excel.models.TextBlockIdentifier;

import java.io.FileNotFoundException;

/**
 * Created by Eadgyo on 12/07/16.
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
     * <p>
     * TextBlockIdentifier define text behavior detection (detect space with min space size, detect new line with min
     * height new line...),  StringFormatter clean extracted data. BlockMerger define merge block behavior (block
     * lines that form paragraph) You can define their behavior by creating modules (StringFormatterModule. They can
     * combined one or more modules. Combining multiples modules just combine their effects. Effects are applied one
     * by one, in added order (the first added is the first applied).
     * </p>
     *
     * @param path                pdf file location
     * @param textBlockIdentifier define text parameters like min space size, or new line min height
     * @param stringFormatter     format extracted text
     * @param blockMerger         merge text block
     *
     * @return transformedData. It keeps page separation and add map for each page. Map allows your to make fast search
     * on extracted data.
     */
    public static TransformedData extractFromFile(String path,
                                                  TextBlockIdentifier textBlockIdentifier,
                                                  StringFormatter stringFormatter,
                                                  BlockMerger blockMerger) throws FileNotFoundException,
                                                                                  IncorrectFileTypeException
    {
        return null;
    }

    /**
     * Sort transformedData in both columns and lines.
     *
     * <p>
     *
     * </p>
     *
     * <p>
     * Each entity in transformedData are processed one by one. Entity is first added to the right column, then in the
     * right
     * line. Columns and lines are composed of lanes and adding process use the same lane adding process.
     * </p>
     *
     * <p> Entity adding in lane process search first for lower lane. If lane exists, entity is compared with
     * existing in lane entities. If there are at least one entity colliding along opposite axis of the lane (Y-axis
     * for columns, X-Axis for lines), insert in higher colliding lane if existing or split current lane. Else if
     * lower lane doesn't exist create a new lane and insert entity in it.
     * </p>
     *
     * @param transformedData extracted transformedData from extractFromFile process
     *
     * @return Sorted Data. Data in transformedData are sorted in the right column and line. It keeps page seperation.
     * Columns
     * and lines contained sorted entities according to Y-axis for columns and X-axis for lines.
     */
    public static SortedData sortTransformedData(TransformedData transformedData,
                                                 BlockMerger blockMerger)
    {
        return null;
    }

    /**
     * Create excel page from intersection of columns and lines computed from sorted data
     *
     * <p>
     *
     * </p>
     *
     * @param sortedData data sorted in column and line
     *
     * @return excel sheet
     */
    public static HSSFSheet createExcelPage(SortedData sortedData)
    {
        return null;
    }
}
