package org.eadge.extractpdfexcel.debug.display;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Base for resized panel
 */
public class JResizedPanelPdf extends JPanel
{
    protected double pdfWidth  = 0;
    protected double pdfHeight = 0;

    public JResizedPanelPdf(double pdfWidth, double pdfHeight)
    {
        this.pdfWidth = pdfWidth;
        this.pdfHeight = pdfHeight;
    }

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
        double scale = Math.max(pdfWidth / getWidth(), pdfHeight / getHeight());
        // Scale render
        //g2d.scale(scale, scale);
    }

    public void clearScreen(Graphics g)
    {
        g.setColor(Color.WHITE);
        g.clearRect(0, 0, getWidth(), getHeight());
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
