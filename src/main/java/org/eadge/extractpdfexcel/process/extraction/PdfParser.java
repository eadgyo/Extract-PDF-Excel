package org.eadge.extractpdfexcel.process.extraction;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import org.eadge.extractpdfexcel.data.ExtractedData;
import org.eadge.extractpdfexcel.data.ExtractedPage;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;
import org.eadge.extractpdfexcel.tools.DefaultSimpleExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


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
     *
     *
     */
    public ArrayList<Block> readFields(int pageIndex)
    {
        ArrayList<Block> blocks = new ArrayList<>();

        AcroFields acroFields = this.pdf.getAcroFields();
        Collection<AcroFields.Item> values = acroFields.getFields().values();
        for (AcroFields.Item item: values) {
            PdfDictionary widget = item.getWidget(0);
            Integer page = item.getPage(0);
            if (pageIndex == page && widget.getAsString(PdfName.DV).toString() == "")
            {
                PdfArray rect = widget.getAsArray(PdfName.RECT);
                PdfString text = widget.getAsString(PdfName.V);
                PdfString font = widget.getAsString(PdfName.DA);
                String[] s = font.toString().split(" ");
                float fontSize = 10.0f;
                if (s.length > 2 && s[0] == "f2")
                {
                    fontSize = Float.valueOf(s[1]);
                }

                float x = rect.getAsNumber(0).floatValue();
                float y = rect.getAsNumber(1).floatValue();
                float width = Math.abs(rect.getAsNumber(2).floatValue() - rect.getAsNumber(0).floatValue());
                float height = Math.abs(rect.getAsNumber(3).floatValue() - rect.getAsNumber(1).floatValue());

                Block block = createBlock(text.toUnicodeString(), new Rectangle2(x, y, width, height), fontSize);
                blocks.add(block);
            }
        }

        return blocks;
    }

    private Block createBlock(String text, Rectangle2 blockRectangle, float fontSize)
    {
        // Set direction to TOP
        Direction blockDirection = Direction.TOP;
        Direction textDirection = Direction.TOP;

        // Add color and font info
        Set<BaseColor> fontColors = new HashSet<>();
        Set<BaseColor>    backColors = new HashSet<>();
        Set<DocumentFont> fonts      = new HashSet<>();

        // Add default colors and font
        /*fontColors.add(BaseColor.BLACK);
        backColors.add(BaseColor.WHITE);
        new DocumentFont(new Font(Font.FontFamily.COURIER, fontSize));
        fonts.add();*/


        // Create and return block with direction, rectangle and info
        return new Block(text.toString().trim(), blockRectangle);
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
        ArrayList<Block> extractedBlocksFields = readFields(pageIndex);

        // Set extractor
        DefaultSimpleExtractor         extractor = new DefaultSimpleExtractor(textBlockIdentifier);

        try
        {
            // Extract content with defined extractor
            parser.processContent(pageIndex, extractor);
            extractor.push();
        }
        catch (IOException e)
        {
            return;
        }

        float pdfWidth  = pdf.getPageSize(pageIndex).getWidth();
        float pdfHeight = pdf.getPageSize(pageIndex).getHeight();

        ArrayList<Block> extractedBlocks = extractor.getExtractedBlocksAndRemovePdfOrientation(pdfWidth, pdfHeight);
        extractedBlocks.addAll(extractedBlocksFields);

        ExtractedPage extractedPage = new ExtractedPage(pdfWidth,
                                                        pdfHeight,
                                                        extractedBlocks);

        // Create a new page to add extracted blocks
        extractedData.insertPage(pageIndex,
                                 extractedPage);
    }

    public void cleanDuplicatedData()
    {
        extractedData.cleanDuplicatedData();
    }

    public void mergeBlocks(double mergeFactor)
    {
        extractedData.mergeBlocks(mergeFactor);
    }

    public void close() {
        this.pdf.close();
    }
}
