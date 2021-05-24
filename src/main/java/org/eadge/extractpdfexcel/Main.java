package org.eadge.extractpdfexcel;

import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import java.io.IOException;

public class Main
{
    public static void main(String args[])
    {
        if (args.length == 0)
        {
            printHelp();
            return;
        }

        if (args.length == 1)
        {
            if (args[0].equals("-h") || args[0].equals("--help"))
            {
                printHelp();
                return;
            }

            System.out.println("No xcl path");
            return;
        }
        Object parameters[] = {1.0, 3.0, 2.0, 0.00001, 1.4, true, 0.0, 0.0};

        String sourcePdf = args[0];
        String renderXCL = args[1];

        for (int i = 2; i < args.length; i++)
        {
            if (i == 7)
            {
                parameters[i] = Boolean.parseBoolean(args[i]);
            }
            else
            {
                parameters[i] = Double.parseDouble(args[i]);
            }
        }

        TextBlockIdentifier textBlockIdentifier = new TextBlockIdentifier((double) parameters[0],
                                                                          (double) parameters[1],
                                                                          (double) parameters[2],
                                                                          (double) parameters[3],
                                                                          (double) parameters[4],
                                                                          (boolean) parameters[5]);

        try
        {
            PdfConverter.createExcelFile(sourcePdf, renderXCL, textBlockIdentifier, 0, 1, (double) parameters[6],
                                         (double) parameters[7]);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void printHelp()
    {
        System.out.println("Parameters: sourcePDFPath renderedXCLPath sameLineThreshold sameBlockFactorX " +
                                   "spaceBlockFactorX thresholdAlongY mergeFactor cleanDuplicated lineFactor " +
                                   "columnFactor");

        System.out.println("");

        System.out.println("    -sameLineThreshold (1.0): Used to determine different block in extract process. Above" +
                                   " " +
                                   "threshold," +
                                   " two letters are considered on different lines.\n");

        System.out.println("    -sameBlockFactorX (3.0): Used to detect different block in extract process. If space " +
                                   "between " +
                                   "two" +
                                   " letters are above characterSpaceSize sameBlockFactorX, it's a different block.\n");

        System.out.println("    -spaceBlockFactorX (2.0): Used to detect space character in extract process. If space" +
                                   " is below " +
                                   "characterSpaceSize sameBlockFactorX and above spaceBlockFactorX, it's a space " +
                                   "character.\n");

        System.out.println("    -thresholdAlongY (0.00001): Used to determine if a block is facing Y or X." +
                                   " We take two extremes points of blocks center line and compare coordinates." +
                                   " If difference is under thresholdAlongY, block is facing toward Y axis. Else if " +
                                   "difference is above thresholdAlongY, block is facing X axis.\n");

        System.out.println("    -mergeFactor (1.4): Define space factor between block to consider as single block.\n");

        System.out.println("    -cleanDuplicated (true): If true, removed duplicated block.\n");

        System.out.println("    -lineFactor (0): line factor for line in xcl file. 0 for default space line.\n");

        System.out.println("    -columnFactor (0): column factor for column in xcl file. 0 for default space column" +
                                   ".\n");
    }
}
