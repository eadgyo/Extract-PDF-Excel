package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;

import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Display blocks and lanes
 */
public class DisplayTools
{
    static void drawBlock(Graphics2D g, Block block)
    {
        // Draw bounds
        Rectangle2 bound = block.getBound();
        drawRect(g, bound);
    }

    static void drawBlockText(Graphics2D g, Block block)
    {
        // Get rect of block
        Rectangle2 bound = block.getBound();

        // Get the formatted text
        String formattedText = block.getFormattedText();

        // If the text is too long, shortcut the text
        if (formattedText.length() > 10)
            formattedText = formattedText.substring(0, 10);

        g.drawString(formattedText, (int) bound.getX(), (int) bound.getY());
    }

    static void drawRect(Graphics g, Rectangle2 rect)
    {
        g.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }
}
