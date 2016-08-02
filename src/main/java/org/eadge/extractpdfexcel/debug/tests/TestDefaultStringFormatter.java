package org.eadge.extractpdfexcel.debug.tests;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.debug.display.FrameCreator;
import org.eadge.extractpdfexcel.tools.DefaultStringFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Test Default string formatter
 */
public class TestDefaultStringFormatter
{
    public static void main(String[] args) throws IOException
    {
        boolean resultStringFormatter = testStringFormatter();
        boolean resultBlockFormatter = testBlockFormatter();

        System.out.println(Result.transformResult(resultStringFormatter) + " String formatter");
        System.out.println(Result.transformResult(resultBlockFormatter) + " Block formatter");
    }


    public static boolean testStringFormatter()
    {
        // Create Default String formatter
        DefaultStringFormatter stringFormatter = new DefaultStringFormatter(10);

        // Check String formatter
        String text = "   Bonjour Me vOici   :    ";

        String expected = "bonjour me voici";
        int expectedStartEnd[] = {3, 19};

        int resultStartEnd[] = {0, 0};
        String result = stringFormatter.format(text, resultStartEnd);

        if (!checkResult(result, resultStartEnd, expected, expectedStartEnd))
            return false;

        text = "JE SUIS LA";

        expected = "je suis la";
        expectedStartEnd[0] = 0;
        expectedStartEnd[1] = 10;

        result = stringFormatter.format(text, resultStartEnd);

        if (!checkResult(result, resultStartEnd, expected, expectedStartEnd))
            return false;

        return true;
    }

    public static boolean testBlockFormatter()
    {
        // Create Default String formatter
        DefaultStringFormatter stringFormatter = new DefaultStringFormatter(10);

        // Create blocks
        Block plat = new Block("    Le plat est prêt à vous", new Rectangle2(87.0, 144.0, 366.0, 40.0));
        Block plat1 = new Block("    Le plat est prêt à vous", new Rectangle2(87.0, 200.0, 366.0, 40.0));

        Block plat2 = new Block("Le plat est prêt à vous :    ", new Rectangle2(87.0, 250, 366.0, 40.0));
        Block plat3 = new Block("Le plat est prêt à vous :    ", new Rectangle2(87.0, 300.0, 366.0, 40.0));

        stringFormatter.formatBlock(plat1);
        stringFormatter.formatBlock(plat3);

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(plat);
        blocks.add(plat1);
        blocks.add(plat2);
        blocks.add(plat3);

        FrameCreator.displayBlocks("Test", 800, 600, blocks);

        // Test end
        if (plat1.getBound().getWidth() <= 0 && plat1.getBound().getHeight() <= 0)
            return false;

        if (plat1.getBound().getX() <= plat.getBound().getX())
            return false;

        if (plat1.getBound().getEndPos(0) != plat.getEndPos(0))
            return false;

        // Test start alignment
        if (plat3.getBound().getWidth() <= 0 && plat3.getBound().getHeight() <= 0)
            return false;

        if (plat3.getBound().getX() != plat2.getBound().getX())
            return false;

        if (plat3.getBound().getEndPos(0) >= plat2.getEndPos(0))
            return false;

        return true;
    }

    public static boolean checkResult(String resultText, int resultStartEnd[], String expectedText, int expectedStartEnd[])
    {
        return resultText.equals(expectedText) && Arrays.equals(resultStartEnd, expectedStartEnd);
    }

}
