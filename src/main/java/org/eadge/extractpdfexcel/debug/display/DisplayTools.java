package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;

import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Display blocks and lanes
 */
public class DisplayTools
{
    public static void drawBlock(Graphics2D g, Block block)
    {
        // Draw bounds
        Rectangle2 bound = block.getBound();
        drawRect(g, bound);
    }

    public static void drawBlockText(Graphics2D g, Block block)
    {
        // Get rect of block
        Rectangle2 bound = block.getBound();

        // Get the formatted text
        String formattedText = block.getFormattedText();

        // Draw text in his block bound and shorten the text if it's too long.
        drawShortTextInBound(g, formattedText, bound);
    }

    public static void drawShortTextInBound(Graphics2D g, String text, Rectangle2 bound)
    {
        // If the text is too long, shorten the text
        if (text.length() > 10)
            text = text.substring(0, 10);

        // Draw text in specified bound
        drawTextInBound(g, text, bound);
    }

    public static void drawTextInBound(Graphics2D g, String text, Rectangle2 bound)
    {
        g.drawString(text, (int) bound.getX() + 5, (int) (bound.getY() + bound.getHeight()/2 + 5));
    }

    static void drawRect(Graphics g, Rectangle2 rect)
    {
        g.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }
}
