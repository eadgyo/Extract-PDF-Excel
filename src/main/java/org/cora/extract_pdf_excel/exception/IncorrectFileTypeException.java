package org.cora.extract_pdf_excel.exception;

import java.io.IOException;

/**
 * Created by eadgyo on 13/07/16.
 */
public class IncorrectFileTypeException extends IOException
{
    public IncorrectFileTypeException(String s)
    {
        super(s);
    }
}
