package de.itemis.graphing.model;

public class FloatingAttachment extends AttachmentBase
{
    public enum EPositioningMode { Radial, XY }

    private final double _posAngleOrX;
    private final double _posDistanceOrY;
    private final EPositioningMode _posMode;

    public FloatingAttachment(Vertex vertex, String id, Size size, double posAngleOrX, double posDistanceOrY, EPositioningMode mode)
    {
        super(vertex, id, size, false);

        _posAngleOrX = posAngleOrX;
        _posDistanceOrY = posDistanceOrY;
        _posMode = mode;
    }

    public double getAngleOrX()
    {
        return _posAngleOrX;
    }

    public double getDistanceOrY()
    {
        return _posDistanceOrY;
    }

    public EPositioningMode getPosMode()
    {
        return _posMode;
    }

}
