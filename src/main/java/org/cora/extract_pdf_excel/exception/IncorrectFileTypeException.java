package org.cora.extract_pdf_excel.exception;

import java.io.IOException;

/**
 * Created by Eadgyo on 13/07/16.
 */
public class IncorrectFileTypeException extends IOException
{
    public IncorrectFileTypeException(String s)
    {
        super(s);
    }
}
