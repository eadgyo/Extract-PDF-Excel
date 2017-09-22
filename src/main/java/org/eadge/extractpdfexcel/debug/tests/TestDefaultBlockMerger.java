package org.eadge.extractpdfexcel.debug.tests;

import com.itextpdf.text.BaseColor;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.block.Direction;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.tools.DefaultBlockMerger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eadgyo on 21/07/16.
 *
 * Test Default block merger
 */
public class TestDefaultBlockMerger
{
    public static void main(String[] args) throws IOException
    {
        boolean resultBlockMerger          = testDefaultBlockMerger();
        System.out.println(Result.transformResult(resultBlockMerger) + " Block merger");
    }

    public static boolean testDefaultBlockMerger()
    {
        DefaultBlockMerger blockMerger = new DefaultBlockMerger();

        Map<String, Block> linkedNames = new HashMap<>();
        ArrayList<Block>   blocks      = new ArrayList<>();

        createBlocks(linkedNames, blocks);

        // Merge blocks
        blockMerger.mergeIfNecessaryBlocks(blocks);

        // Test Distance
        if (wereMerged(linkedNames, "Distance0", "Distance1"))
            return false;

        if (!wereMerged(linkedNames, "Near0", "Near1"))
            return false;

        // Test Alignements
        if (wereMerged(linkedNames, "NotAlign0", "NotAlign1") || wasMerged(linkedNames, "NotAlign2"))
            return false;

        if (!wereMerged(linkedNames, "Align0", "Align1") && !wereMerged(linkedNames, "Align2", "Align3"))
            return false;

        // Test matching types
        if (wereMerged(linkedNames, "DiffBackC0", "DiffBackC1"))
            return false;

        if (!wereMerged(linkedNames, "MatchTypes0", "MatchTypes1"))
            return false;

        // Test orientation
        if (wereMerged(linkedNames, "DiffAxisOr0", "DiffAxisOr1"))
            return false;

        if (wereMerged(linkedNames, "DiffTextOr0", "DiffTextOr1"))
            return false;

        return true;
    }

    private static void createBlocks(Map<String, Block> linkedNames, ArrayList<Block> blocks)
    {
        linkedNames.put("Distance0", new Block("Distance0",new Rectangle2(36.0, 14.0, 99.0, 22.0)));
        blocks.add(linkedNames.get("Distance0"));
        linkedNames.put("Distance1", new Block("Distance1",new Rectangle2(35.0, 47.0, 98.0, 21.0)));
        blocks.add(linkedNames.get("Distance1"));

        linkedNames.put("NotAlign0", new Block("NotAlign0",new Rectangle2(120.0, 120.0, 39.0, 18.0)));
        blocks.add(linkedNames.get("NotAlign0"));
        linkedNames.put("NotAlign1", new Block("NotAlign1",new Rectangle2(38.0, 141.0, 135.0, 17.0)));
        blocks.add(linkedNames.get("NotAlign1"));
        linkedNames.put("NotAlign2", new Block("NotAlign2",new Rectangle2(59.0, 163.0, 58.0, 17.0)));
        blocks.add(linkedNames.get("NotAlign2"));

        BaseColor color0 = new BaseColor(1, 1, 1);
        BaseColor color1 = new BaseColor(1, 2, 1);
        linkedNames.put("DiffBackC0", new Block("DiffBackC0",new Rectangle2(38.0, 268.0, 130.0, 23.0)));
        blocks.add(linkedNames.get("DiffBackC0"));
        linkedNames.get("DiffBackC0").addBackColor(color0);
        linkedNames.put("DiffBackC1", new Block("DiffBackC1",new Rectangle2(38.0, 294.0, 130.0, 21.0)));
        blocks.add(linkedNames.get("DiffBackC1"));
        linkedNames.get("DiffBackC1").addBackColor(color1);

        linkedNames.put("DiffAxisOr0", new Block("DiffAxisOr0",new Rectangle2(39.0, 342.0, 123.0, 23.0)));
        blocks.add(linkedNames.get("DiffAxisOr0"));
        linkedNames.get("DiffAxisOr0").setBlockOrientation(Direction.TOP);
        linkedNames.put("DiffAxisOr1", new Block("DiffAxisOr1",new Rectangle2(39.0, 370.0, 121.0, 23.0)));
        blocks.add(linkedNames.get("DiffAxisOr1"));

        linkedNames.put("DiffTextOr0", new Block("DiffTextOr0",new Rectangle2(225.0, 16.0, 130.0, 18.0)));
        blocks.add(linkedNames.get("DiffTextOr0"));
        linkedNames.get("DiffTextOr0").setTextOrientation(Direction.TOP);
        linkedNames.put("DiffTextOr1", new Block("DiffTextOr1",new Rectangle2(225.0, 39.0, 130.0, 16.0)));
        blocks.add(linkedNames.get("DiffTextOr1"));

        linkedNames.put("Align0", new Block("Align0",new Rectangle2(492.0, 52.0, 123.0, 17.0)));
        blocks.add(linkedNames.get("Align0"));
        linkedNames.put("Align1", new Block("Align1",new Rectangle2(550.0, 34.0, 68.0, 14.0)));
        blocks.add(linkedNames.get("Align1"));
        linkedNames.put("Align2", new Block("Align2",new Rectangle2(519.0, 72.0, 72.0, 12.0)));
        blocks.add(linkedNames.get("Align2"));
        linkedNames.put("Align3", new Block("Align3",new Rectangle2(517.0, 88.0, 52.0, 13.0)));
        blocks.add(linkedNames.get("Align3"));

        linkedNames.put("Near0", new Block("Near0",new Rectangle2(496.0, 149.0, 122.0, 19.0)));
        blocks.add(linkedNames.get("Near0"));
        linkedNames.put("Near1", new Block("Near1",new Rectangle2(495.0, 170.0, 121.0, 18.0)));
        blocks.add(linkedNames.get("Near1"));

        linkedNames.put("MatchTypes0", new Block("MatchTypes0",new Rectangle2(491.0, 227.0, 127.0, 16.0)));
        blocks.add(linkedNames.get("MatchTypes0"));
        linkedNames.put("MatchTypes1", new Block("MatchTypes1",new Rectangle2(491.0, 246.0, 128.0, 19.0)));
        blocks.add(linkedNames.get("MatchTypes1"));

    }

    private static boolean wasMerged(Map<String, Block> linkedNames, String block)
    {
        return ! linkedNames.get(block).getOriginalText().equals(block);
    }

    private static boolean wereMerged(Map<String, Block> linkedNames, String block0, String block1)
    {
        return wasMerged(linkedNames, block0) || wasMerged(linkedNames, block1);
    }
}
