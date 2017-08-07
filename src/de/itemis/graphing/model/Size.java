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

    public Size addPadding(Padding p)
    {
        return new Size(_width + p.getEast() + p.getWest(), _height + p.getNorth() + p.getSouth());
    }

    public static Size max(Size a, Size b)
    {
        double maxWidth = Math.max(a.getWidth(), b.getWidth());
        double maxHeight = Math.max(a.getHeight(), b.getHeight());
        return new Size(maxWidth, maxHeight);
    }
}
