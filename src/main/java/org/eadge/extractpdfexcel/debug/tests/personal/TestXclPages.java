package org.eadge.extractpdfexcel.debug.tests.personal;

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.SortedData;
import org.eadge.extractpdfexcel.data.XclPage;
import org.eadge.extractpdfexcel.debug.display.FrameCreator;
import org.eadge.extractpdfexcel.debug.tests.TestSorter;

import java.io.IOException;
import java.util.ArrayList;

public class TestXclPages
{
    public static void main(String[] args) throws IOException
    {
        testXcl();
    }

    public static boolean testXcl()
    {
        SortedData sortedData = createSortedPageEx("test/pdf/quiz.pdf");

        ArrayList<XclPage> excelPages = PdfConverter.createExcelPages(sortedData);

        FrameCreator.displayXclPages("Xcl", 800, 600, excelPages);

        return true;
    }

    public static SortedData createSortedPageEx(String source)
    {
        return PdfConverter.sortExtractedData(TestSorter.createExtractedDataEx(source), 0, 1);
    }
}
