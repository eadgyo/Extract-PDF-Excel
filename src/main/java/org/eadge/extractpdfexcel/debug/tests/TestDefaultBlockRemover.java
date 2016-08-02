package org.eadge.extractpdfexcel.debug.tests;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.tools.DefaultBlockRemover;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Test Default Block Remover
 */
public class TestDefaultBlockRemover
{
    public static void main(String[] args) throws IOException
    {
        boolean resultBlockRemoved = testDefaultBlockRemover();
        System.out.println(Result.transformResult(resultBlockRemoved) + " Block remover");
    }

    public static boolean testDefaultBlockRemover()
    {
        // Create blocks
        ArrayList<Block> blocks = createBlocks();

        // Save the number of In block
        int nIn = countIn(blocks);

        // Create boundaries
        Rectangle2 bounds = new Rectangle2(112.0, 35.0, 543.0, 347.0);

        // Create block remover
        DefaultBlockRemover blockRemover = new DefaultBlockRemover(bounds);

        // Remove blocks out of bounds
        blockRemover.removeBlock(blocks);

        // The number of In block must be equal to number of blocks and equals to the the saved number of In.
        return (nIn == countIn(blocks)) && (nIn == blocks.size());
    }

    public static int countIn(ArrayList<Block> blocks)
    {
        // Count number of block In
        int nIn = 0;

        for (Block block : blocks)
        {
            if (block.getOriginalText().equals("In"))
            {
                nIn++;
            }
        }

        return nIn;
    }

    public static ArrayList<Block> createBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<>();

        blocks.add(new Block("In",new Rectangle2(209.0, 194.0, 265.0, 103.0)));
        blocks.add(new Block("In",new Rectangle2(323.0, 57.0, 156.0, 104.0)));
        blocks.add(new Block("In",new Rectangle2(625.0, 179.0, 50.0, 50.0)));
        blocks.add(new Block("In",new Rectangle2(92.0, 279.0, 36.0, 212.0)));
        blocks.add(new Block("In",new Rectangle2(5.0, 105.0, 156.0, 46.0)));
        blocks.add(new Block("In",new Rectangle2(188.0, 47.0, 107.0, 126.0)));

        blocks.add(new Block("Out",new Rectangle2(347.0, 403.0, 50.0, 50.0)));
        blocks.add(new Block("Out",new Rectangle2(504.0, 394.0, 203.0, 23.0)));
        blocks.add(new Block("Out",new Rectangle2(690.0, 31.0, 31.0, 314.0)));
        blocks.add(new Block("Out",new Rectangle2(206.0, 392.0, 78.0, 47.0)));
        blocks.add(new Block("Out",new Rectangle2(162.0, 13.0, 51.0, 15.0)));
        blocks.add(new Block("Out",new Rectangle2(11.0, 51.0, 78.0, 23.0)));
        blocks.add(new Block("Out",new Rectangle2(15.0, 157.0, 88.0, 105.0)));

        return blocks;
    }
}
