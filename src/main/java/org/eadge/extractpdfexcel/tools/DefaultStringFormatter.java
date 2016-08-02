package org.eadge.extractpdfexcel.tools;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.models.StringFormatter;

/**
 * Created by eadgyo on 12/07/16.
 * <p/>
 * Format string:
 * <p>- Transform upCase characters in lowerCase.</p>
 * <p>- Remove space at the start and at the end of the text.</p>
 * <p>- Remove : at the end of the sentence.</p>
 *
 */
public class DefaultStringFormatter extends StringFormatter
{
    /**
     * Size of space character
     */
    private int sizeSpaceCharacter;

    public DefaultStringFormatter(int sizeSpaceCharacter)
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
    public static String format(String rawText, int[] outputStartEndRawText)
    {
        int startTextIndex = trimStart(rawText);
        int endTextIndex = trimEnd(rawText);

        // If last char is a :, remove character and update end of sentence
        if ((endTextIndex - 1) > startTextIndex &&
                rawText.charAt(endTextIndex - 1) == ':')
        {
            // Remove letter
            endTextIndex--;
            // UpdateEnd of sentence
            endTextIndex = trimEnd(rawText, startTextIndex, endTextIndex);
        }

        // Get subString
        String substring = rawText.substring(startTextIndex, endTextIndex);

        // Store start and end indexes
        outputStartEndRawText[0] = startTextIndex;
        outputStartEndRawText[1] = endTextIndex;

        // Return text in lowerCase
        return substring.toLowerCase();
    }

    /**
     * Find the start of sentence, after spaces characters
     * @param text used text
     * @return start index in text
     */
    private static int trimStart(String text)
    {
        int i;
        for (i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) != ' ')
            {
                break;
            }
        }
        return i;
    }

    /**
     * Find the end of sentence, before spaces characters
     * @param text used text
     * @return end index in text
     */
    private static int trimEnd(String text)
    {
        int i;
        for (i = text.length() - 1; i >= 0; i--)
        {
            if (text.charAt(i) != ' ')
            {
                break;
            }
        }
        return i + 1;
    }

    /**
     * Find the end of sentence, before spaces characters
     * @param text used text
     * @param startOfText used start of text
     * @param endOfText used end of texts
     * @return end index in text
     */
    private static int trimEnd(String text, int startOfText, int endOfText)
    {
        int i;
        for (i = endOfText - 1; i >= startOfText; i--)
        {
            if (text.charAt(i) != ' ')
            {
                break;
            }
        }
        return i + 1;
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
     * Update bounds length after changing start and end. By default, it update size by removing total size of removed
     * letters. Size of removed letters is computing as size of space character.
     *
     * @param startEndRawText newStart and newEnd indexes.
     * @param rawText         text without formatting, used to compute change in end index, and update bound end.
     * @param modifiedBound   rectangle being modified, updating it size from newStart and newEnd indexes.
     */
    private void updateSizeBoundOfBlock(int startEndRawText[], String rawText, Rectangle2 modifiedBound)
    {
        // Compute start and end difference
        // (newIndex - lastIndex) * sizeOfSpaceCharacter
        double deltaStart = startEndRawText[0] * sizeSpaceCharacter;
        double deltaEnd   = (rawText.length() - startEndRawText[1]) * sizeSpaceCharacter;

        // Update start and end of bound
        modifiedBound.setX(modifiedBound.getX() + deltaStart);
        modifiedBound.setWidth(modifiedBound.getWidth() - deltaEnd - deltaStart);

        // If rectangle width is negative or null, we just approximate width as series of space characters
        if (modifiedBound.getWidth() < 0)
        {
            modifiedBound.setWidth((startEndRawText[1] - startEndRawText[0]) * sizeSpaceCharacter);
        }
    }
}
