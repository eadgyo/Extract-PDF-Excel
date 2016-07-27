package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;

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

    public JPanelLane(double pdfWidth, double pdfHeight)
    {
        super(pdfWidth, pdfHeight);
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
        }
    }
}
