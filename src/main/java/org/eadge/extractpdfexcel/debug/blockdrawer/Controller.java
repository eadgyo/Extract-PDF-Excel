package org.eadge.extractpdfexcel.debug.blockdrawer;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.data.geom.Vector2;

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
    private ModifyAction modifyAction;
    private CreateAction createAction;

    public Controller()
    {
        model = new Model();
        view = new View();
        view.drawer.setModel(model);

        // Create actions
        GenerateAction generateAction = new GenerateAction();
        ClearAction    clearAction    = new ClearAction();
        createAction = new CreateAction();
        deleteAction = new DeleteAction();
        modifyAction = new ModifyAction();

        // Init bottom action buttons
        view.generate.setAction(generateAction);
        view.clear.setAction(clearAction);
        view.delete.setAction(deleteAction);
        view.createNewBlock.setAction(createAction);

        // Init right clicks action buttons
        view.createNewBlockMenu.setAction(createAction);
        view.deleteMenu.setAction(deleteAction);
        view.modifyMenu.setAction(modifyAction);

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
            // If double click
            if (mouseEvent.getClickCount() == 2)
            {
                Vector2 vector2     = new Vector2(mouseEvent.getX(), mouseEvent.getY());
                Block   actualBlock = model.getSelected();
                if (actualBlock != null)
                {
                    // Update the selected point
                    model.setSelectedPointAndResetIfSame(vector2);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent)
        {
            Vector2 vector2 = new Vector2(mouseEvent.getX(), mouseEvent.getY());

            // Save coordinates for possible block creation
            createAction.setSavedXandY(mouseEvent.getX(), mouseEvent.getY());

            // If press right click
            if (mouseEvent.getButton() == MouseEvent.BUTTON3)
            {
                // Update selected block
                model.setSelected(vector2);

                // Show popup menu
                view.popupMenu.show(view, mouseEvent.getX(), mouseEvent.getY());
            }
            else if (mouseEvent.getButton() == MouseEvent.BUTTON1) // Press left click
            {
                // If there was a selected block
                if (model.getSelected() != null)
                {
                    // If one point is already selected
                    if (model.getSelectedPoint() != -1)
                    {
                        // Update the selected point
                        model.setSelectedPoint(vector2);

                        // If no points are selected
                        if (model.getSelectedPoint() == -1)
                        {
                            // It may not be this block
                            // Update selected block
                            model.setSelected(vector2);
                        }
                    }
                    else
                    {
                        // No point selected
                        // Update the selected block
                        model.setSelected(vector2);
                    }
                }
                else
                {
                    // There were no blocks selected
                    // Update the selected block
                    model.setSelected(vector2);
                }
                lastMousePos = vector2;
            }
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

            // If there are no bocks selected
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

    private class ModifyAction extends AbstractAction
    {
        public ModifyAction()
        {
            super("Modify");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            // If one block is selected
            Block selected = model.getSelected();

            // Change his text
            String originalText = JOptionPane.showInputDialog(view,
                                                              "Text du block",
                                                              selected.getOriginalText());

            // If originalText has changed
            if (originalText != null)
            {
                // Update block content
                model.changeSelectedText(originalText);
            }
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

            // Update modify possibility
            modifyAction.setEnabled(model.getSelected() != null);
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
        private int savedX, savedY;

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
                Block block = new Block(originalText, new Rectangle2(savedX, savedY, 50, 50));
                model.addBlock(block);
            }
        }

        public void setSavedX(int savedX)
        {
            this.savedX = savedX;
        }

        public void setSavedY(int savedY)
        {
            this.savedY = savedY;
        }

        public void clearXandY()
        {
            savedX = 10;
            savedY = 10;
        }

        public void setSavedXandY(int x, int y)
        {
            savedX = x;
            savedY = y;
        }
    }
}
