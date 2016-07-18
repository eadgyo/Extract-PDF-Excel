package org.cora.extract_pdf_excel.models;

/**
 * Created by Eadgyo on 14/07/16.
 */

import com.itextpdf.awt.geom.Rectangle;
import org.cora.extract_pdf_excel.data.block.Block;

import java.util.Collection;

/**
 * Format text.
 */
public abstract class StringFormatter
{
    /**
     * Size of space character
     */
    private int sizeSpaceCharacter;

    public StringFormatter(int sizeSpaceCharacter)
    {
        this.sizeSpaceCharacter = sizeSpaceCharacter;
    }

    public void setSpaceSize(int spaceSize)
    {
        this.sizeSpaceCharacter = spaceSize;
    }

    /**
     * Format text using desired format model.
     *
     * @param rawText               unformatted text.
     * @param outputStartEndRawText newStart and newEnd are new index for the formatted text depending on rawText. They
     *                              are stored in an array of 2 int. Array have to be initialized before calling this
     *                              method. Negative value for the newStart means that rawText size have been extended
     *                              from start. In the same way, newEnd index after the end of rawText means that
     *                              rawText have been extended from end.
     *
     * @return formatted text.
     */
    public abstract String format(String rawText, int outputStartEndRawText[]);

    /**
     * Update bound size from newStart and newEnd. By default, it update size by removing total size of removed letters.
     * Size of removed letters is computing as size of space character.
     *
     * @param startEndRawText newStart and newEnd indexes.
     * @param rawText         text without formatting, used to compute change in end index, and update bound end.
     * @param modifiedBound   rectangle being modified, updating it size from newStart and newEnd indexes.
     */
    public void updateSizeBoundOfBlock(int startEndRawText[], String rawText, Rectangle modifiedBound)
    {
        // Compute start and end difference
        // (newIndex - lastIndex) * sizeOfSpaceCharacter
        double deltaStart = startEndRawText[0] * sizeSpaceCharacter;
        double deltaEnd = (startEndRawText[1] - rawText.length()) * sizeSpaceCharacter;

        // Update start and end of bound
        modifiedBound.x = modifiedBound.x - deltaStart;
        modifiedBound.width = modifiedBound.width - deltaEnd;

        // If rectangle width is negative or null, we just approximate width as series of space characters
        modifiedBound.width = rawText.length() * sizeSpaceCharacter;
    }

    /**
     * Format text in raw block and replace unformatted text with formatted.
     *
     * @param rawBlock block containing unformatted text.
     */
    public void formatBlock(Block rawBlock)
    {
        // Will store newStart and newEnd of the formattedText.
        // Will be used to update rectangle size of rawBlock
        int startEndRawText[] = new int[2];

        // Get formatted text as rawText to allow combining multiple formatting.
        String formattedText = format(rawBlock.getFormattedText(), startEndRawText);

        // Update size from newStart and newEnd
        updateSizeBoundOfBlock(startEndRawText, rawBlock.getFormattedText(), rawBlock.getBound());

        // Update formattedText
        rawBlock.setFormattedText(formattedText);
    }

    /**
     * Format collection of blocks containing unformatted text.
     *
     * @param rawBlocks collection of blocks containing unformatted text.
     */
    public void formatBlocks(Collection<Block> rawBlocks)
    {
        for (Block rawBlock : rawBlocks)
        {
            formatBlock(rawBlock);
        }
    }
}
