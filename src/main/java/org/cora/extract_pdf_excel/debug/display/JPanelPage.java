package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.ExtractedPage;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Panel to render extracted page content
 */
public class JPanelPage extends JPanelResized
{
    private ExtractedPage extractedPage = null;

    public void setExtractedPage(ExtractedPage extractedPage)
    {
        this.extractedPage = extractedPage;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        assert (extractedPage != null);

        Graphics2D g2d = (Graphics2D) g;

        g.setFont(new Font("TimesNewRoman", Font.ROMAN_BASELINE, 10));

        ArrayList<Block> blocks = extractedPage.getBlocks();

        // Display all blocks
        for (Block block : blocks)
        {
            g.setColor(Color.gray);

            // Draw bounds
            Rectangle2 bound = block.getBound();
            super.drawRect(g, bound);

            // Get the formatted text
            String formattedText = block.getFormattedText();
            // If the text is too long, shortcut the text
            if (formattedText.length() > 10)
                formattedText = formattedText.substring(0, 10);
            // Draw th formatted text
            g2d.drawString(formattedText, (int) bound.getX(), (int) bound.getY());
        }
    }
}
