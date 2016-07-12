package org.cora.extract_pdf_excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.cora.extract_pdf_excel.data.ArrangedData;
import org.cora.extract_pdf_excel.data.RawData;
import org.cora.extract_pdf_excel.models.StringFormater;
import org.cora.extract_pdf_excel.models.TextBlockIdentifier;

/**
 * Created by Eadgyo on 12/07/16.
 */

/**
 * Extract data from pdf and convert it to excel page
 * <p>
 * Full conversion process is divided in 3 operations <br/>
 * 1-Extract Raw Data from PDF <br/>
 * 2-Arrange Raw Data in colums and lines <br/>
 * 3-Create Excel page from Arranged Data <br/>
 * </p>
 * <p>
 * Operations must be done in designated order.
 * </p>
 */
public class PdfConverter
{

    /**
     * Extract Raw Data from PDF
     * <p>
     *
     * </p>
     *
     * <p>
     * StringFormater and TextBlockIdentifier are used to clean extracted data
     * You can define their behavior by creating modules (StringFormaterModule
     * && TextBlockIdentifierModule) StringFormater and TextBlockIdentifier can
     * combined one or more modules. Combining mutiples modules just combine
     * their effects. Effects are applied one by one, and the first added is the
     * first applied.
     * </p>
     *
     * @param path                pdf file location
     * @param stringFormater      extracted text formater
     * @param textBlockIdentifier extracted block identifier
     *
     * @return Raw Data
     */
    public static RawData extractFromFile(String path,
                                          StringFormater stringFormater,
                                          TextBlockIdentifier textBlockIdentifier)
    {
        return null;
    }

    /**
     * @param rawData
     *
     * @return
     */
    public static ArrangedData arrangeData(RawData rawData)
    {
        return null;
    }

    public static HSSFSheet createExcelPage(ArrangedData arrangedData)
    {
        return null;
    }
}
