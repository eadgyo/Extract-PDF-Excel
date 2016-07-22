package org.cora.extract_pdf_excel.debug.blockdrawer;

import org.cora.extract_pdf_excel.data.block.Block;
import org.cora.extract_pdf_excel.data.geom.Rectangle2;
import org.cora.extract_pdf_excel.data.geom.Vector2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Controller of drawer
 */
public class Controller
{
    private View         view;
    private Model        model;
    private DeleteAction deleteAction;

    public Controller()
    {
        model = new Model();
        view = new View();
        view.drawer.setModel(model);

        GenerateAction generateAction = new GenerateAction();
        ClearAction    clearAction    = new ClearAction();
        deleteAction = new DeleteAction();
        CreateAction createAction = new CreateAction();

        view.generate.setAction(generateAction);
        view.clear.setAction(clearAction);
        view.delete.setAction(deleteAction);
        view.createNewBlock.setAction(createAction);

        UpdateText updateText = new UpdateText();
        model.addObserver(updateText);

        MyMouseListener myMouseListener = new MyMouseListener();
        view.drawer.addMouseListener(myMouseListener);
        view.drawer.addMouseMotionListener(myMouseListener);

        model.init();

        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setVisible(true);
    }

    private class MyMouseListener implements MouseMotionListener, MouseListener
    {
        private Vector2 lastMousePos = null;

        @Override
        public void mouseClicked(MouseEvent mouseEvent)
        {
            if (mouseEvent.getClickCount() == 2)
            {
                Vector2 vector2     = new Vector2(mouseEvent.getX(), mouseEvent.getY());
                Block   actualBlock = model.getSelected();
                if (actualBlock != null)
                {
                    // Update the selected point
                    model.setSelectedPoint(vector2);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent)
        {
            Vector2 vector2     = new Vector2(mouseEvent.getX(), mouseEvent.getY());
            Block   actualBlock = model.getSelected();

            // Update selected
            if (model.getSelected() == null)
            {
                model.setSelectedPoint(vector2);

                if (model.getSelectedPoint() == -1)
                    model.setSelected(vector2);
            }
            lastMousePos = vector2;
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent)
        {

        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent)
        {
            Block actualBlock = model.getSelected();
            if (actualBlock != null)
            {
                Vector2 vector2 = new Vector2(mouseEvent.getX(), mouseEvent.getY());

                // If a point is selected
                if (model.getSelectedPoint() != -1)
                {
                    // Move point of the block
                    model.movePoint(vector2);
                }
                else
                {
                    // Move entire block
                    Vector2 translate = new Vector2(vector2, lastMousePos);
                    model.translate(translate);
                }

                lastMousePos = vector2;
            }
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent)
        {

        }
    }

    private class GenerateAction extends AbstractAction
    {
        public GenerateAction()
        {
            super("Generate");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            view.textBlock.setText(model.toString());
        }
    }

    private class ClearAction extends AbstractAction
    {
        public ClearAction()
        {
            super("Clear");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            model.clear();
            view.textBlock.setText("");
        }
    }

    private class UpdateText implements Observer
    {
        @Override
        public void update(Observable observable, Object o)
        {
            // Update text
            view.textBlock.setText(model.toString());

            // Update panel
            view.drawer.repaint();

            // Update delete possibility
            deleteAction.setEnabled(model.getSelected() != null);
        }
    }

    private class DeleteAction extends AbstractAction
    {
        public DeleteAction()
        {
            super("Delete");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            model.removeSelected();
        }
    }

    private class CreateAction extends AbstractAction
    {
        public CreateAction()
        {
            super("Create");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            String originalText = JOptionPane.showInputDialog(view,
                                                              "Text du block",
                                                              "test");

            if (originalText != null)
            {
                Block block = new Block(originalText, new Rectangle2(10, 10, 50, 50));
                model.addBlock(block);
            }
        }
    }
}
