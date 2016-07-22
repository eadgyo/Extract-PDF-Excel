package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.geom.Rectangle2;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Base for resized panel
 */
public class JPanelResized extends JPanel
{
    private double pdfWidth  = 0;
    private double pdfHeight = 0;

    public void setPdfWidth(double pdfWidth)
    {
        this.pdfWidth = pdfWidth;
    }

    public void setPdfHeight(double pdfHeight)
    {
        this.pdfHeight = pdfHeight;
    }

    public void autoScale(Graphics2D g2d)
    {
        // Get the minimum scale factor
        double scale = Math.min(getWidth()/ pdfWidth, getHeight()/ pdfHeight);
        // Scale render
        g2d.scale(scale, scale);
    }

    public void clearScreen(Graphics g)
    {
        g.setColor(Color.WHITE);
        g.clearRect(0, 0, getWidth(), getHeight());
    }

    public void drawRect(Graphics g2d, Rectangle2 rect)
    {
        g2d.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        assert (pdfWidth != 0 && pdfHeight != 0);

        clearScreen(g);
        autoScale(g2d);

    }
}
