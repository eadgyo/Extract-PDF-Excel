package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.ExtractedPage;
import org.cora.extract_pdf_excel.data.block.Block;

import java.awt.*;
import java.util.Collection;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Panel to render extracted page content
 */
public class JPanelPage extends JResizedPanelPdf
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

        Collection<Block> blocks = extractedPage.getBlocks();

        // Display all blocks
        for (Block block : blocks)
        {
            g.setColor(Color.GRAY);
            DisplayTools.drawBlock(g2d, block);
            DisplayTools.drawBlockText(g2d, block);
        }
    }
}
