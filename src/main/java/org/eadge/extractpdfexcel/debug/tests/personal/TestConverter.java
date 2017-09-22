package org.eadge.extractpdfexcel.debug.tests.personal;

import org.eadge.extractpdfexcel.PdfConverter;

import java.io.IOException;

public class TestConverter
{
    public static void main(String[] args) throws IOException
    {
        PdfConverter.createExcelFile("test/pdf/example.pdf", "test/pdf/example.xcl");
    }
}
