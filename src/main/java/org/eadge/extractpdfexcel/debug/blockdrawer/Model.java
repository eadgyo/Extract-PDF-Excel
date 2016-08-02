package org.eadge.extractpdfexcel.debug.blockdrawer;

import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.geom.Rectangle2;
import org.eadge.extractpdfexcel.data.geom.Vector2;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by eadgyo on 22/07/16.
 *
 * Model of drawer
 */
public class Model extends Observable
{
    private ArrayList<Block> blocks = new ArrayList<>();
    private Block actualBlock = null;
    private int selectedPoint = 0;

    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }

    public void init()
    {
        setChanged();
        notifyObservers();
    }

    public void setBlocks(ArrayList<Block> blocks)
    {
        this.blocks = blocks;

        setChanged();
        notifyObservers();
    }

    public void addBlock(Block block)
    {
        this.blocks.add(block);

        setChanged();
        notifyObservers();
    }

    public void clear()
    {
        this.blocks.clear();

        actualBlock = null;
        selectedPoint = -1;

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("ArrayList<Block> blocks = new ArrayList<>();\n");

        for (int i = 0; i < blocks.size(); i++)
        {
            Block block =  blocks.get(i);
            builder.append("blocks.add(")
                   .append(Tools.textGeneratingBlockFast(block, i))
                   .append(");")
                   .append("\n");
        }
        return builder.toString();
    }

    public void setSelected(Vector2 vector)
    {
        Block savedBlock = actualBlock;

        actualBlock = null;

        for (Block block : blocks)
        {
            if (block.getBound().contains(vector))
            {
                if (actualBlock == null || savedBlock == null || actualBlock == savedBlock)
                    actualBlock = block;
                if (actualBlock != savedBlock)
                    break;
            }
        }

        if (savedBlock != actualBlock || savedBlock == null)
            selectedPoint = -1;

        setChanged();
        notifyObservers();
    }

    public Block getSelected()
    {
        return actualBlock;
    }

    public int getSelectedPoint()
    {
        return selectedPoint;
    }

    public void setSelectedPointAndResetIfSame(Vector2 point)
    {
        if (actualBlock == null)
            return;

        int selectedPoint = getSelectedPoint(point);

        if (selectedPoint == this.selectedPoint)
            this.selectedPoint = -1;
        else
            this.selectedPoint = selectedPoint;

        setChanged();
        notifyObservers();
    }

    public void setSelectedPoint(Vector2 point)
    {
        if (actualBlock == null)
            return;

        this.selectedPoint = getSelectedPoint(point);

        setChanged();
        notifyObservers();
    }

    public int getSelectedPoint(Vector2 point)
    {
        if (actualBlock == null)
            return -1;

        int        selectedPoint = -1;
        Rectangle2 rect          = actualBlock.getBound();
        double     sqDist        = Double.MAX_VALUE;

        for (int i = 0; i < 4; i++)
        {
            Vector2 rectPoint = rect.getPoint(i);
            double dist = rectPoint.getSqMagnitude(point);
            if (dist < sqDist && dist < 150)
            {
                selectedPoint = i;
                sqDist = dist;
            }
        }

        return selectedPoint;
    }

    public void movePoint(Vector2 vector2)
    {
        if (actualBlock == null || selectedPoint == -1)
            return;

        actualBlock.getBound().movePoint(selectedPoint, vector2);

        setChanged();
        notifyObservers();
    }

    public void move(Vector2 vector2)
    {
        if (actualBlock == null)
            return;

        actualBlock.getBound().move(vector2);

        setChanged();
        notifyObservers();
    }

    public void removeSelected()
    {
        if (actualBlock == null)
            return;

        blocks.remove(actualBlock);

        actualBlock = null;
        selectedPoint = -1;

        setChanged();
        notifyObservers();
    }

    public void translate(Vector2 vector2)
    {
        if (actualBlock == null)
            return;

        actualBlock.getBound().translate(vector2);

        setChanged();
        notifyObservers();
    }

    public void changeSelectedText(String originalText)
    {
        if (actualBlock == null)
            return;

        actualBlock.setOriginalText(originalText);
        actualBlock.setFormattedText(originalText);

        setChanged();
        notifyObservers();
    }
}
