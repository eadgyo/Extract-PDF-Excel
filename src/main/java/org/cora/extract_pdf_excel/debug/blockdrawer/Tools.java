package org.cora.extract_pdf_excel.debug.blockdrawer;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Tools for drawer
 */
public class Tools
{
    public static String textGeneratingBlock(Block block, int i)
    {
        Rectangle2 rect = block.getBound();
        String createRect = "Rectangle2 rect" + i + " = new Rectangle2(" + rect.getPos(0) + ", " + rect.getPos(1) + ", " + rect.getLength(0)
                + ", " + rect.getLength(1) + ");";
        String createBlock = "Block block" + i + " = new Block(\"" + block.getOriginalText() + "\", rect" + i + ");";
        return createRect + "\n" + createBlock + "\n";
    }
}
