package org.cora.extract_pdf_excel.debug.blockdrawer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 22/07/16.
 *
 * View of drawer
 */
public class View extends JFrame
{
    public Drawer drawer;
    public JTextArea textBlock;
    public JButton createNewBlock;
    public JButton generate;
    public JButton clear;
    public JButton delete;

    public View()
    {
        super("Block creator");
        this.setSize(800, 600);
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        this.drawer = new Drawer();
        this.drawer.setPreferredSize(new Dimension(800, 400));
        this.drawer.setBorder(BorderFactory.createLineBorder(Color.black));
        this.add(drawer);

        this.textBlock = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textBlock);
        this.add(scrollPane);

        Container buttonContainer = new Container();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.LINE_AXIS));

        createNewBlock = new JButton();
        generate = new JButton();
        clear = new JButton();
        delete = new JButton();
        buttonContainer.add(createNewBlock);
        buttonContainer.add(generate);
        buttonContainer.add(clear);
        buttonContainer.add(delete);

        this.setLocationRelativeTo(null);
        this.add(buttonContainer);
    }
}
