package org.eadge.extractpdfexcel.debug.blockdrawer;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;

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

    public static String textGeneratingBlockFast(Block block, int i)
    {
        Rectangle2 rect = block.getBound();
        return "new Block(\"" + block.getOriginalText() + "\"," + "new Rectangle2(" + rect.getPos(0) + ", " + rect.getPos(1) + ", " + rect.getLength(0)
                + ", " + rect.getLength(1) + "))";
    }
}
