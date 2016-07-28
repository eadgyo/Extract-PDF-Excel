package org.cora.extract_pdf_excel.data.geom;

import java.util.Arrays;

/**
 * Created by eadgyo on 17/07/16.
 * <p/>
 * Rectangle2 with getter using indexes.
 */
public class Rectangle2 implements Cloneable
{
    public static final int X = 0;
    public static final int Y = 1;

    private double coordinates[];
    private double length[];

    public Rectangle2()
    {
        coordinates = new double[2];
        length = new double[2];
    }

    public Rectangle2(double x, double y, double width, double height)
    {
        this();
        set(x, y, width, height);
    }

    public void set(double x, double y, double width, double height)
    {
        setPos(x, y);
        setLength(width, height);
    }

    public void set(Rectangle2 rect)
    {
        set(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public Rectangle2 clone()
    {
        try
        {
            Rectangle2 rect = (Rectangle2) super.clone();

            rect.coordinates = new double[2];
            rect.length = new double[2];

            rect.set(getX(), getY(), getWidth(), getHeight());

            return rect;
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set the extrem points of the rectangle
     * @param xStart origin point x coordinate
     * @param yStart origin point y coordinate
     * @param xEnd end point x coordinate
     * @param yEnd end point y coordinate
     */
    public void setStartEndPos(double xStart, double yStart, double xEnd, double yEnd)
    {
        // Switch if needed
        if (xStart > xEnd)
        {
            double temp = xStart;
            xStart = xEnd;
            xEnd = temp;
        }

        if (yStart > yEnd)
        {
            double temp = yStart;
            yStart = yEnd;
            yEnd = temp;
        }

        // Set origin point coordinates
        setPos(xStart, yStart);

        // Set length
        setLength(xEnd - xStart, yEnd - yStart);
    }

    public void setPos(double x, double y)
    {
        setX(x);
        setY(y);
    }

    public double getX()
    {
        return getPos(X);
    }

    public void setX(double x)
    {
        setPos(X, x);
    }

    public double getY()
    {
        return getPos(Y);
    }

    public void setY(double y)
    {
        setPos(Y, y);
    }

    public void setLength(double width, double height)
    {
        setWidth(width);
        setHeight(height);
    }

    public double getWidth()
    {
        return getLength(X);
    }

    public void setWidth(double width)
    {
        setLength(X, width);
    }

    public double getHeight()
    {
        return getLength(Y);
    }

    public void setHeight(double height)
    {
        setLength(Y, height);
    }

    public double getPos(int i)
    {
        return coordinates[i];
    }

    public double getLength(int i)
    {
        return length[i];
    }

    public void setPos(int i, double v)
    {
        coordinates[i] = v;
    }

    public void addPos(int i, double v)
    {
        coordinates[i] += v;
    }

    public void setLength(int i, double v)
    {
        length[i] = v;
    }

    public void addLength(int i, double v)
    {
        length[i] += v;
    }

    public double getMidPos(int i)
    {
        return coordinates[i] + length[i]*0.5;
    }

    public double getEndPos(int i)
    {
        return coordinates[i] + length[i];
    }

    /**
     * Check if a vector is inside the rectangle
     *
     * @param vector2 checked rect
     * @return true if the vector is inside, false otherwise
     */
    public boolean contains(Vector2 vector2)
    {
        return containsAlongOneAxis(X, vector2) && containsAlongOneAxis(Y, vector2);
    }

    /**
     * Check if a vector is inside the rectangle along one axis
     *
     * @param axis used axis
     * @param vector2 checked rect
     *
     * @return true if the vector is inside alonng one axis, false otherwise
     */
    public boolean containsAlongOneAxis(int axis, Vector2 vector2)
    {
        return vector2.get(axis) > getPos(axis) && vector2.get(axis) < getEndPos(axis);
    }

    /**
     * Check if a rect is colliding with the rectangle
     * @param rect checked rect
     * @return true if they are colliding, false otherwise
     */
    public boolean areColliding(Rectangle2 rect)
    {
        return areCollidingAlongOneAxis(X, rect) && areCollidingAlongOneAxis(Y, rect);
    }

    /**
     * Check if a rect is colliding with the rectangle along one axis
     *
     * @param axis used axis
     * @param rect checked rect
     *
     * @return true if they are colliding along axis, false otherwise
     */
    public boolean areCollidingAlongOneAxis(int axis, Rectangle2 rect)
    {
        return getPos(axis) < rect.getEndPos(axis) && getEndPos(axis) > rect.getPos(axis);
    }

    public Vector2 getPoint(int i)
    {
        switch (i)
        {
            case 0:
                return new Vector2(getX(), getY());
            case 1:
                return new Vector2(getEndPos(X), getY());
            case 2:
                return new Vector2(getEndPos(X), getEndPos(Y));
            case 3:
                return new Vector2(getX(), getEndPos(Y));
            default:
                return null;
        }
    }

    public void movePoint(int point, Vector2 vector2)
    {
        Vector2 start = getPoint(0);
        Vector2 end = getPoint(2);

        switch (point)
        {
            case 0:
                start.set(vector2);
                break;
            case 1:
                start.setY(vector2.getY());
                end.setX(vector2.getX());
                break;
            case 2:
                end.set(vector2);
                break;
            case 3:
                start.setX(vector2.getX());
                end.setY(vector2.getY());
                break;
        }

        setStartEndPos(start.getX(), start.getY(), end.getX(), end.getY());
    }

    /**
     * Move the rectangle to the specified position
     *
     * @param vector2 specified position
     */
    public void move(Vector2 vector2)
    {
        setPos(vector2.getX(), vector2.getY());
    }

    @Override
    public String toString()
    {
        return "Rect{ coor=" + Arrays.toString(coordinates) +
                ", length=" + Arrays.toString(length) +
                " }";
    }

    public void translate(Vector2 vector2)
    {
        setPos(getX() + vector2.getX(), getY() + vector2.getY());
    }
}
