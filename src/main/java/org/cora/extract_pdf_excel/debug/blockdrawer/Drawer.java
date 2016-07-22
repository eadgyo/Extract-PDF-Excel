package org.cora.extract_pdf_excel.debug.blockdrawer;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;
import org.cora.extract_pdf_excel.data.geom.Vector2;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Drawer
 */
public class Drawer extends JPanel
{
    private Model model;
    
    public void setModel(Model model)
    {
        this.model = model;
    }

    @Override
    public void paint(Graphics graphics)
    {
        graphics.clearRect(0, 0, getWidth(), getHeight());
        super.paint(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics;
        ArrayList<Block> blocks = model.getBlocks();

        // Draw all rect
        graphics.setColor(Color.GRAY);
        for (Block block : blocks)
        {
            Rectangle2 rect = block.getBound();
            graphics2D.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());
        }

        // Draw selected block
        Block actualBlock = model.getSelected();
        if (actualBlock != null)
        {
            graphics.setColor(Color.RED);
            Rectangle2 rect = actualBlock.getBound();
            graphics2D.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight());

            // If one point is selected
            if (model.getSelectedPoint() != -1)
            {
                // Draw point as a circle
                int index = model.getSelectedPoint();
                Vector2 selectedPoint = rect.getPoint(index);
                graphics.fillOval((int) selectedPoint.getX() - 3, (int) selectedPoint.getY() - 3, 6, 6);
            }
        }
    }
}
