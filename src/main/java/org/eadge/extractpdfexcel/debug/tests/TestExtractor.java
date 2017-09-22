package org.eadge.extractpdfexcel.debug.tests;

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.ExtractedData;
import org.eadge.extractpdfexcel.data.ExtractedPage;
import org.eadge.extractpdfexcel.debug.display.FrameCreator;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Unit testing for pdf extractor
 */
public class TestExtractor
{
    public static void main(String[] args) throws IOException
    {
        boolean result = testExtractor();

        System.out.println(Result.transformResult(result) + " Test extractor");
    }

    /**
     *
     * @return success
     */
    public static boolean testExtractor()
    {
        // Create default textBlock identifier
        TextBlockIdentifier blockIdentifier = new TextBlockIdentifier();

        // Load the right pdf
        try
        {
            ExtractedData extractedPdf = PdfConverter.extractFromFile("test/pdf/test.pdf", blockIdentifier);

            Collection<ExtractedPage> pages = extractedPdf.getPagesCollection();

            // Display extracted pages
            for (ExtractedPage page : pages)
            {
                FrameCreator.displayExtractedPage("Test extracted blocks", page);
            }
        }
        catch (FileNotFoundException | IncorrectFileTypeException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

        /*
    public static void testTransformOrientation()
    {
        Rectangle2 rect = new Rectangle2(100, 100, 100, 20);

        ArrayList<Rectangle2> rects = new ArrayList<>();

        rects.add(rect);
        rects.add(DefaultSimpleExtractor.transformBottomTop(rect, 600));
        rects.add(DefaultSimpleExtractor.transformLeftTop(rect));
        rects.add(DefaultSimpleExtractor.transformRightTop(rect, 800, 600));

        FrameCreator.displayRect("Test rect", rects);
    }*/

}
