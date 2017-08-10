package de.itemis.graphing.model;

public class Coordinates
{
    private final double _x;
    private final double _y;

    public Coordinates(double x, double y)
    {
        _x = x;
        _y = y;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }
}
