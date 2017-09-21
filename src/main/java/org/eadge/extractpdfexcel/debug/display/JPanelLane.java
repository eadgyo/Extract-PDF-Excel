package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.data.lane.Lanes;

import java.awt.*;
import java.util.Collection;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Display lanes bounds
 */
public class JPanelLane  extends JResizedPanelPdf
{
    private Lanes lanes = null;
    private boolean drawBlocks;

    public JPanelLane(double pdfWidth, double pdfHeight, boolean drawBlocks)
    {
        super(pdfWidth, pdfHeight);
        this.drawBlocks = drawBlocks;
    }

    public void setLanes(Lanes lanes)
    {
        this.lanes = lanes;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        assert (lanes != null);

        Graphics2D g2d = (Graphics2D) g;

        Collection<Lane> lanes = this.lanes.getLanes();

        g.setColor(Color.GRAY);

        // Draw each lane
        for (Lane lane : lanes)
        {
            DisplayTools.drawRect(g2d, lane.getBound());

            if (drawBlocks)
            {
                // Display block text content
                Collection<Block> blocksCollection = lane.getBlocksCollection();
                for (Block block : blocksCollection)
                {
                    DisplayTools.drawBlock(g2d, block);
                    DisplayTools.drawBlockText(g2d, block);
                }
            }
        }
    }
}
