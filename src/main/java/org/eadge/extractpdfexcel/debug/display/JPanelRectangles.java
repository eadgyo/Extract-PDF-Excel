package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.geom.Rectangle2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Draw rect
 */
public class JPanelRectangles extends JPanel
{
    private ArrayList<Rectangle2> rectangles = null;

    public void setRectangles(ArrayList<Rectangle2> rectangles)
    {
        this.rectangles = rectangles;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.GRAY);

        for (Rectangle2 rect : rectangles)
        {
            DisplayTools.drawRect(g2d, rect);
        }
    }
}
