package de.itemis.graphing.model;

import de.itemis.graphing.model.style.BlockStyle;

public class Attachment extends BaseGraphElement implements ISized
{
    public enum ELocation { North, East, South, West }

    private final Vertex _parent;
    private final double _width;
    private final double _height;
    private final ELocation _location;
    private BlockStyle _style = null;

    public Attachment(Vertex vertex, String id, double width, double height, ELocation location)
    {
        super(vertex.getGraph(), id);
        _parent = vertex;
        _width = width;
        _height = height;
        _location = location;
        setStyle((BlockStyle) _graph.getDefaultAttachmentStyle().getCopy());
    }

    public Vertex getParent()
    {
        return _parent;
    }

    @Override
    public double getBaseWidth()
    {
        return _width;
    }

    @Override
    public double getBaseHeight()
    {
        return _height;
    }

    @Override
    public double getFinalWidth()
    {
        return _width;
    }

    @Override
    public double getFinalHeight()
    {
        return _height;
    }

    public ELocation getLocation()
    {
        return _location;
    }

    @Override
    public BlockStyle getStyle()
    {
        return _style;
    }

    public void setStyle(BlockStyle style)
    {
        _style = (BlockStyle)style.getCopy();
        _style.setParent(this);
        styleChanged();
    }

}
