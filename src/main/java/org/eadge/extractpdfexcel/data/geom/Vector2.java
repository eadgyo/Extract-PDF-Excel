package org.eadge.extractpdfexcel.data.geom;

import java.util.Arrays;

/**
 * Created by eadgyo on 22/07/16.
 *
 * 2D Vector
 */
public class Vector2
{
    private static final int X = 0;
    private static final int Y = 1;

    private double coordinates[];

    public Vector2()
    {
        coordinates = new double[2];
    }

    public Vector2(double x, double y)
    {
        this();

        set(x, y);
    }

    public Vector2(Vector2 B, Vector2 A)
    {
        this(B.getX() - A.getX(), B.getY() - A.getY());
    }

    public void set(double x, double y)
    {
        setX(x);
        setY(y);
    }

    public double getX()
    {
        return coordinates[X];
    }

    public double getY()
    {
        return coordinates[Y];
    }

    public double get(int i)
    {
        return coordinates[i];
    }

    public void setX(double x)
    {
        coordinates[X] = x;
    }

    public void setY(double y)
    {
        coordinates[Y] = y;
    }

    public void set(int i, double v)
    {
        coordinates[i] = v;
    }

    public void addX(double v) { coordinates[X] += v; }

    public void addY(double v) { coordinates[Y] += v; }

    public void add(int i, double v) { coordinates[i] += v; }

    public double getMagnitude()
    {
        return getMagnitude(this);
    }

    public double getSqMagnitude()
    {
        return getSqMagnitude(this);
    }

    public double getMagnitude(Vector2 v)
    {
        return Math.sqrt(getSqMagnitude(v));
    }

    public double getSqMagnitude(Vector2 v)
    {
        return (getX() - v.getX())*(getX() - v.getX()) + (getY() - v.getY())*(getY() - v.getY());
    }

    public void set(Vector2 vector2)
    {
        set(vector2.getX(), vector2.getY());
    }

    @Override
    public String toString()
    {
        return "Vec2" + Arrays.toString(coordinates);
    }
}
