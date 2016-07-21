package org.cora.extract_pdf_excel.tools;

import org.cora.extract_pdf_excel.models.StringFormatter;

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
    public DefaultStringFormatter(int sizeSpaceCharacter)
    {
        super(sizeSpaceCharacter);
    }

    @Override
    public String format(String rawText, int[] outputStartEndRawText)
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
            trimEnd(rawText, startTextIndex, endTextIndex);
        }

        // Get subString
        String substring = rawText.substring(startTextIndex, endTextIndex);

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
    public static int trimEnd(String text, int startOfText, int endOfText)
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
}
