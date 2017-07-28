package de.itemis.graphing.model;

public class Size
{
    private final double _width;

    private final double _height;

    public Size(double width, double height)
    {
        _width = width;
        _height = height;
    }

    public double getWidth()
    {
        return _width;
    }

    public double getHeight()
    {
        return _height;
    }

    public Size addPadding(double padding)
    {
        return new Size(_width + padding*2, _height + padding*2);
    }
}
