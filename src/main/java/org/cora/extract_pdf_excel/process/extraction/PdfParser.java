package org.cora.extract_pdf_excel.process.extraction;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import org.cora.extract_pdf_excel.data.ExtractedData;
import org.cora.extract_pdf_excel.data.ExtractedPage;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.models.TextBlockIdentifier;
import org.cora.extract_pdf_excel.tools.DefaultSimpleExtractor;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by eadgyo on 15/07/16.
 * <p/>
 * Extract text from pdf keeping text separated in pages.
 */
public class PdfParser
{
    private final TextBlockIdentifier    textBlockIdentifier;
    private final PdfReader              pdf;
    private final PdfReaderContentParser parser;
    private final ExtractedData          extractedData;

    public PdfParser(PdfReader pdf, TextBlockIdentifier textBlockIdentifier)
    {
        this.pdf = pdf;
        this.textBlockIdentifier = textBlockIdentifier;
        this.parser = new PdfReaderContentParser(pdf);
        this.extractedData = new ExtractedData();
    }

    public ExtractedData getExtractedData()
    {
        return extractedData;
    }

    /**
     * Read all pages in pdf and store extracted data in extractedData variable
     */
    public void readAllPage()
    {
        for (int i = 1; i <= pdf.getNumberOfPages(); i++)
        {
            readPage(i);
        }
    }

    /**
     * Read page at index in pdf and store extracted data in extractedData variable
     *
     * @param pageIndex index of read page
     */
    public void readPage(int pageIndex)
    {
        // Set extractor
        DefaultSimpleExtractor extractor = new DefaultSimpleExtractor(textBlockIdentifier);
        try
        {
            // Extract content with defined extractor
            parser.processContent(pageIndex, extractor);
        }
        catch (IOException e)
        {
            return;
        }

        float pdfWidth  = pdf.getPageSize(pageIndex).getWidth();
        float pdfHeight = pdf.getPageSize(pageIndex).getHeight();

        ArrayList<Block> extractedBlocks = extractor.getExtractedBlocksAndRemovePdfOrientation(pdfWidth, pdfHeight);

        ExtractedPage extractedPage = new ExtractedPage(pdfWidth,
                                                        pdfHeight,
                                                        extractedBlocks);

        // Create a new page to add extracted blocks
        extractedData.insertPage(pageIndex,
                                 extractedPage);
    }
}
