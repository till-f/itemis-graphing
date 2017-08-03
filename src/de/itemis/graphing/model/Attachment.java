package de.itemis.graphing.model;

public class Attachment extends GraphElement implements ISized
{
    public enum ELocation { North, East, South, West }

    private final Vertex _parent;
    private final Size _innerSize;
    private final double _padding;
    private final ELocation _location;
    private final boolean _affectDynamicLayout;

    public Attachment(Vertex vertex, String id, Size innerSize, double padding, ELocation location, boolean affectDynamicLayout)
    {
        super(vertex.getGraph(), id);
        _parent = vertex;
        _innerSize = innerSize;
        _padding = padding;
        _location = location;
        _affectDynamicLayout = affectDynamicLayout;
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
        return _innerSize;
    }

    @Override
    public Size getOuterSize()
    {
        return _innerSize.addPadding(_padding);
    }

    public double getPadding()
    {
        return _padding;
    }

    public ELocation getLocation()
    {
        return _location;
    }

    public boolean isDynamicLayoutAffected()
    {
        return _affectDynamicLayout;
    }

}
