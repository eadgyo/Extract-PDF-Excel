package org.cora.extract_pdf_excel.debug.tests;

import org.cora.extract_pdf_excel.PdfConverter;
import org.cora.extract_pdf_excel.data.ExtractedPage;
import org.cora.extract_pdf_excel.data.SortedPage;
import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;
import org.cora.extract_pdf_excel.data.lane.Lane;
import org.cora.extract_pdf_excel.data.lane.Lanes;
import org.cora.extract_pdf_excel.debug.display.FrameCreator;
import org.cora.extract_pdf_excel.process.arrangement.BlockSorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by eadgyo on 27/07/16.
 *
 * Test sort process
 */
public class TestSorter
{
    public static void main(String[] args) throws IOException
    {
        //boolean resultBlockSorterExtractedPage = testBlockSorterExtractedPage();
        boolean resultSortExtractedPage = testSortExtractedPage();
        //System.out.println(Result.transformResult(resultBlockSorterExtractedPage) + " Block sorter");
        System.out.println(Result.transformResult(resultSortExtractedPage) + " Sort extracted page");
    }

    public static boolean testBlockSorterExtractedPage()
    {
        ExtractedPage extractedPage = createExtractedPage2();

        Collection<Block> blocks = extractedPage.getBlocks();

        Lanes columns = new Lanes();

        for (Block block : blocks)
        {
            BlockSorter.insertInLanes(SortedPage.DEFAULT_COLUMN_AXIS,
                                      SortedPage.DEFAULT_OPPOSITE_COLUMN_AXIS,
                                      block,
                                      columns);
        }

        FrameCreator.displayBlocks("Blocks", 800, 600, blocks);
        FrameCreator.displayLanes("Lanes", 800, 600, columns);

        if (columns.size() != 2)
            return false;

        Map.Entry<Double, Lane> higherLaneEntry = columns.getHigherLaneEntry(0);

        Lane lane0 = higherLaneEntry.getValue();
        Lane lane1 = columns.getHigherLane(higherLaneEntry.getKey());

        if (lane0.size() == 0 || lane1.size() == 0)
            return false;

        Collection<Block> blocks0 = lane0.getBlocksCollection();

        for (Block block : blocks0)
        {
            if (!block.getFormattedText().equals("Nom") &&
                    !block.getFormattedText().equals("Prenom") &&
                    !block.getFormattedText().contains("Fiche"))
                return false;
        }

        Collection<Block> blocks1 = lane1.getBlocksCollection();

        for (Block block : blocks1)
        {
            if (!block.getFormattedText().equals("Elvis") &&
                    !block.getFormattedText().equals("NoName"))
                return false;
        }

        return true;
    }

    public static ExtractedPage createExtractedPage2()
    {
        ExtractedPage extractedPage = new ExtractedPage(800, 600);

        ArrayList<Block> blocks = new ArrayList<>();

        blocks.add(new Block("Fiche presentation spectateur", new Rectangle2(29.0, 15.0, 717.0, 19.0)));

        blocks.add(new Block("Nom", new Rectangle2(44.0, 90.0, 56.0, 15.0)));
        blocks.add(new Block("Prenom", new Rectangle2(50.0, 110.0, 50.0, 17.0)));

        blocks.add(new Block("Elvis", new Rectangle2(122.0, 87.0, 63.0, 12.0)));
        blocks.add(new Block("NoName", new Rectangle2(121.0, 109.0, 100.0, 17.0)));

        extractedPage.addAllBlocks(blocks);

        return extractedPage;
    }

    public static boolean testSortExtractedPage()
    {
        ExtractedPage extractedPage = createExtractedPage();

        SortedPage sortedPage = PdfConverter.sortExtractedPage(extractedPage, 0, 1);

        FrameCreator.displaySortedPage("Sorted Page", sortedPage);

        return true;
    }

    public static ExtractedPage createExtractedPage()
    {
        ExtractedPage extractedPage = new ExtractedPage(800, 600);

        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(new Block("Nom", new Rectangle2(44.0, 90.0, 56.0, 15.0)));
        blocks.add(new Block("Prenom", new Rectangle2(50.0, 110.0, 50.0, 17.0)));
        blocks.add(new Block("Age", new Rectangle2(48.0, 133.0, 52.0, 12.0)));
        blocks.add(new Block("Elvis", new Rectangle2(122.0, 87.0, 63.0, 12.0)));
        blocks.add(new Block("NoName", new Rectangle2(121.0, 109.0, 100.0, 17.0)));
        blocks.add(new Block("45", new Rectangle2(120.0, 136.0, 26.0, 10.0)));
        blocks.add(new Block("Fiche presentation spectateur", new Rectangle2(29.0, 15.0, 717.0, 19.0)));
        blocks.add(new Block("Date", new Rectangle2(260.0, 83.0, 118.0, 15.0)));
        blocks.add(new Block("Signature", new Rectangle2(284.0, 109.0, 76.0, 18.0)));
        blocks.add(new Block("Signé", new Rectangle2(391.0, 105.0, 80.0, 31.0)));
        blocks.add(new Block("05/10/2014", new Rectangle2(390.0, 81.0, 101.0, 19.0)));
        blocks.add(new Block("Dates", new Rectangle2(202.0, 203.0, 10.0, 49.0)));
        blocks.add(new Block("Lieux", new Rectangle2(244.0, 232.0, 58.0, 17.0)));
        blocks.add(new Block("Dates", new Rectangle2(352.0, 198.0, 11.0, 44.0)));
        blocks.add(new Block("Test", new Rectangle2(47.0, 229.0, 57.0, 19.0)));
        blocks.add(new Block("Habilitations", new Rectangle2(117.0, 232.0, 57.0, 13.0)));
        blocks.add(new Block("Plombier", new Rectangle2(134.0, 261.0, 25.0, 10.0)));
        blocks.add(new Block("Agriculteur", new Rectangle2(116.0, 282.0, 52.0, 18.0)));
        blocks.add(new Block("01/2016", new Rectangle2(188.0, 260.0, 32.0, 11.0)));
        blocks.add(new Block("02/2070", new Rectangle2(181.0, 286.0, 50.0, 14.0)));
        blocks.add(new Block("Paris", new Rectangle2(246.0, 260.0, 52.0, 13.0)));
        blocks.add(new Block("NewYork", new Rectangle2(243.0, 284.0, 65.0, 19.0)));
        blocks.add(new Block("#1", new Rectangle2(52.0, 257.0, 50.0, 15.0)));
        blocks.add(new Block("#2", new Rectangle2(52.0, 285.0, 50.0, 17.0)));
        blocks.add(new Block("02/2014", new Rectangle2(320.0, 257.0, 72.0, 17.0)));
        blocks.add(new Block("05/2016", new Rectangle2(318.0, 286.0, 76.0, 17.0)));
        blocks.add(new Block("Habilitation", new Rectangle2(203.0, 175.0, 144.0, 14.0)));
        blocks.add(new Block("JE sais", new Rectangle2(146.0, 340.0, 144.0, 20.0)));
        blocks.add(new Block("Faire la table", new Rectangle2(304.0, 341.0, 112.0, 18.0)));
        blocks.add(new Block("Ranger les couverts", new Rectangle2(305.0, 371.0, 124.0, 13.0)));
        blocks.add(new Block("Je sais 2", new Rectangle2(147.0, 369.0, 143.0, 17.0)));

        extractedPage.addAllBlocks(blocks);

        return extractedPage;
    }
}
