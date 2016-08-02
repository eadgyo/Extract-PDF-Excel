package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.block.Block;

import java.awt.*;
import java.util.Collection;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Panel to render extracted page content
 */
public class JPanelBlocks extends JResizedPanelPdf
{
    private Collection<Block> blocks = null;

    public JPanelBlocks(double pdfWidth, double pdfHeight)
    {
        super(pdfWidth, pdfHeight);
    }

    public void setBlocks(Collection<Block> blocks)
    {
        this.blocks = blocks;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        assert (blocks != null);

        Graphics2D g2d = (Graphics2D) g;

        g.setFont(new Font("TimesNewRoman", Font.ROMAN_BASELINE, 10));

        // Display all blocks
        for (Block block : blocks)
        {
            g.setColor(Color.GRAY);
            DisplayTools.drawBlock(g2d, block);
            DisplayTools.drawBlockText(g2d, block);
        }
    }
}
