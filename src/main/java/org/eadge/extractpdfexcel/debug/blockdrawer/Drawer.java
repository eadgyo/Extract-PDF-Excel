package org.eadge.extractpdfexcel.debug.blockdrawer;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Vector2;
import org.eadge.extractpdfexcel.debug.display.DisplayTools;

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

        Graphics2D       graphics2D = (Graphics2D) graphics;
        ArrayList<Block> blocks     = model.getBlocks();

        // Draw all rect
        graphics.setColor(Color.DARK_GRAY);
        for (Block block : blocks)
        {
            DisplayTools.drawBlock(graphics2D, block);
            DisplayTools.drawBlockText(graphics2D, block);
        }

        // Draw selected block
        Block actualBlock = model.getSelected();
        if (actualBlock != null)
        {
            graphics.setColor(Color.RED);
            DisplayTools.drawBlock(graphics2D, actualBlock);

            // If one point is selected
            if (model.getSelectedPoint() != -1)
            {
                // Draw point as a circle
                int     index         = model.getSelectedPoint();
                Vector2 selectedPoint = actualBlock.getBound().getPoint(index);
                graphics.fillOval((int) selectedPoint.getX() - 5, (int) selectedPoint.getY() - 5, 10, 10);
            }
        }
    }
}
