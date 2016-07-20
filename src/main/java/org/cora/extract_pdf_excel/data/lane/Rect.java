package org.cora.extract_pdf_excel.data.lane;

import com.itextpdf.awt.geom.Dimension;
import com.itextpdf.awt.geom.Point;
import com.itextpdf.awt.geom.Rectangle;

/**
 * Created by eadgyo on 17/07/16.
 * <p/>
 * Rectangle with getter using indexes.
 */
public class Rect extends Rectangle
{
    public Rect()
    {
    }

    public Rect(Point p)
    {
        super(p);
    }

    public Rect(Point p, Dimension d)
    {
        super(p, d);
    }

    public Rect(double x, double y, double width, double height)
    {
        super(x, y, width, height);
    }

    public Rect(int width, int height)
    {
        super(width, height);
    }

    public Rect(Rectangle r)
    {
        super(r);
    }

    public Rect(com.itextpdf.text.Rectangle r)
    {
        super(r);
    }

    public Rect(Dimension d)
    {
        super(d);
    }

    public double getPos(int i)
    {
        if (i == 0)
        {
            return x;
        }
        else if (i == 1)
        {
            return y;
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public double getLength(int i)
    {
        switch (i)
        {
            case 0:
                return width;
            case 1:
                return height;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public void setPos(int i, double v)
    {
        if (i == 0)
        {
            x = v;
        }
        else if (i == 1)
        {
            y = v;
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }

    public void setLength(int i, double v)
    {
        switch (i)
        {
            case 0:
                width = v;
                break;
            case 1:
                height = v;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public void addLength(int i, double v)
    {
        switch (i)
        {
            case 0:
                width += v;
                break;
            case 1:
                height += v;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }
    }
}
