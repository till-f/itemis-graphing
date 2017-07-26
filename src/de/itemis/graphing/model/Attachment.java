package de.itemis.graphing.model;

public class Attachment extends BaseGraphElement
{
    private final double _radius;
    private final double _degree;

    public Attachment(String id, double radius, double degree)
    {
        super(id);
        _radius = radius;
        _degree = degree;
    }

    public double getRadius()
    {
        return _radius;
    }

    public double getDegree()
    {
        return _degree;
    }
}
