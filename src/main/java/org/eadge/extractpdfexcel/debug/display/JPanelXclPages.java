package org.eadge.extractpdfexcel.debug.display;

import org.eadge.extractpdfexcel.data.XclPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JPanelXclPages extends JPanel
{
    private JComboBox<String> comboBox = new JComboBox<>(new DefaultComboBoxModel<String>());
    private List<XclPage> pages;
    private JPanel cardPanel = new JPanel(new CardLayout());
    private double pdfWidth, pdfHeight;

    public JPanelXclPages(double pdfWidth, double pdfHeight)
    {
        super();
        this.pdfWidth = pdfWidth;
        this.pdfHeight = pdfHeight;

        setLayout(new BorderLayout());
        comboBox.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                CardLayout cl = (CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, (String) comboBox.getSelectedItem());
            }
        });

        add(BorderLayout.SOUTH, comboBox);
        add(BorderLayout.CENTER, cardPanel);
    }

    public void setXclPages(Collection<XclPage> xclPages)
    {
        pages = new ArrayList<>(xclPages);

        updateComboBox();
        updateCardLayout();
    }

    private void updateComboBox()
    {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();

        model.removeAllElements();

        // Create combo box model
        for (int i = 0; i < pages.size(); i++)
        {
            model.addElement("" + i);
        }
    }

    private void updateCardLayout()
    {
        cardPanel.removeAll();
        for (int i = 0; i < pages.size(); i++)
        {
            JPanelXcl jPanelXcl = new JPanelXcl(pdfWidth, pdfHeight);
            jPanelXcl.setXclPage(pages.get(i));
            cardPanel.add("" + i, jPanelXcl);
        }

        this.repaint();
    }
}
