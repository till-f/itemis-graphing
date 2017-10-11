package de.itemis.graphing.model;

public class FloatingAttachment extends AttachmentBase
{
    private final double _posAngle;
    private final double _posDistance;

    public FloatingAttachment(Vertex vertex, String id, Size size, double posAngle, double posDistance)
    {
        super(vertex, id, size, false);

        _posAngle = posAngle;
        _posDistance = posDistance;
    }

    public double getAngle()
    {
        return _posAngle;
    }

    public double getDistance()
    {
        return _posDistance;
    }
}
