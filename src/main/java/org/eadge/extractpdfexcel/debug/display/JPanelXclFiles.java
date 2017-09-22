package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.XclPage;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JPanelXclFiles extends JPanel
{
    private JComboBox<String> filesSelector;
    private List<String> xclFiles;
    private JPanelXclPages jPanelXclPages;

    public JPanelXclFiles(double pdfWidth, double pdfHeight, List<String> xclFiles)
    {
        this.setLayout(new BorderLayout());
        jPanelXclPages = new JPanelXclPages(pdfWidth, pdfHeight);
        this.xclFiles = xclFiles;

        String fileNames[] = new String[xclFiles.size()];
        xclFiles.toArray(fileNames);

        JPanel southContainer = new JPanel(new BorderLayout());
        JButton openButton = new JButton("Open");

        openButton.addActionListener(new OpenPDF());

        filesSelector = new JComboBox<>(fileNames);
        filesSelector.addActionListener(new ItemChanged());

        // Set the first file
        if (xclFiles.size() != 0)
        {
            // Get the corresponding file
            String firstFileName = fileNames[0];
            try
            {
                ArrayList<XclPage> xclPages = getXCLPages(firstFileName);
                jPanelXclPages.setXclPages(xclPages);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IncorrectFileTypeException e)
            {
                e.printStackTrace();
            }
        }

        southContainer.add(filesSelector, BorderLayout.CENTER);
        southContainer.add(openButton, BorderLayout.EAST);

        add(southContainer, BorderLayout.SOUTH);
        add(jPanelXclPages, BorderLayout.CENTER);
    }

    private class ItemChanged implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            String firstFileName = (String) filesSelector.getSelectedItem();
            try
            {
                ArrayList<XclPage> xclPages = getXCLPages(firstFileName);
                jPanelXclPages.setXclPages(xclPages);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IncorrectFileTypeException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class OpenPDF implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            if (Desktop.isDesktopSupported())
            {
                String firstFileName = (String) filesSelector.getSelectedItem();
                try
                {
                    File myFile = new File(firstFileName);
                    Desktop.getDesktop().open(myFile);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }

        }
    }

    private ArrayList<XclPage> getXCLPages(String source) throws FileNotFoundException, IncorrectFileTypeException
    {
        return PdfConverter.convertFileToXclPages(source);
    }


}
