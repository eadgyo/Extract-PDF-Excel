package org.cora.extract_pdf_excel.debug.display;

import org.cora.extract_pdf_excel.data.array.My2DArray;
import org.cora.extract_pdf_excel.data.block.Block;

import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * JPanel to display excel array
 */
public class JPanelXcl extends JPanelResized
{
    private My2DArray<Block> arrayBlocks;

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
    }
}
