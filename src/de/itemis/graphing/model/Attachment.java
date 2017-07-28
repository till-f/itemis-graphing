package de.itemis.graphing.model;

public class Attachment extends BaseGraphElement implements ISized
{
    public enum ELocation { North, East, South, West }

    private final Vertex _parent;
    private final Size _size;
    private final ELocation _location;

    public Attachment(Vertex vertex, String id, Size size, ELocation location)
    {
        super(vertex.getGraph(), id);
        _parent = vertex;
        _size = size;
        _location = location;
        setSelectable(false);
        setStyle(EStyle.Regular, _graph.getDefaultAttachmentStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, _graph.getDefaultAttachmentStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, _graph.getDefaultAttachmentStyle(EStyle.Selected));
    }

    public Vertex getParent()
    {
        return _parent;
    }

    @Override
    public Size getInnerSize()
    {
        return _size;
    }

    @Override
    public Size getOuterSize()
    {
        return _size;
    }

    public ELocation getLocation()
    {
        return _location;
    }
}
