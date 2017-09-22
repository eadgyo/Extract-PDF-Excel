package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.XclPage;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.data.geom.Vector2;

import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * JPanel to display excel page of cells
 */
public class JPanelXcl extends JResizedPanelPdf
{
    private XclPage xclPage = null;

    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;

    private static boolean noLength = true;

    public JPanelXcl(double pdfWidth, double pdfHeight)
    {
        super(pdfWidth, pdfHeight);
    }

    public void setXclPage(XclPage xclPage)
    {
        this.xclPage = xclPage;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if (xclPage == null)
            return;

        Graphics2D g2d = (Graphics2D) g;

        // Init the cursor position
        Vector2 cursorPosition = new Vector2(0, 0);

        // Parse the lines and cols
        // Display each block
        g.setColor(Color.GRAY);

        for (int col = 0; col < xclPage.numberOfColumns(); col++)
        {
            double columnWidth = (int) (pdfWidth / xclPage.numberOfColumns());
            if (!noLength)
                columnWidth = (int) xclPage.getColumnWidth(col);

            // Reset cursorPosition to the first line
            cursorPosition.set(Y_AXIS, 0);

            for (int line = 0; line < xclPage.numberOfLines(); line++)
            {

                double lineHeight = (int) (pdfHeight / xclPage.numberOfLines());
                if (!noLength)
                    lineHeight = (int) xclPage.getLineHeight(line);

                g2d.setColor(Color.GRAY);

                // Create cell rectangle
                Rectangle2 rectangle = new Rectangle2(cursorPosition.getX(), cursorPosition.getY(), columnWidth, lineHeight);

                // Draw cell rect
                DisplayTools.drawRect(g2d, rectangle);

                // Move the cursor along line opposite axis
                cursorPosition.add(Y_AXIS, lineHeight);

                // Get the cell content
                Block block = xclPage.getBlockAt(col, line);

                // If there is a block
                if (block != null)
                {
                    g2d.setColor(Color.DARK_GRAY);

                    // Draw block text content
                    DisplayTools.drawShortTextInBound(g2d, block.getFormattedText(), rectangle);
                }
            }

            // Move the cursor along column opposite axis
            cursorPosition.add(X_AXIS, columnWidth);
        }
    }
}
