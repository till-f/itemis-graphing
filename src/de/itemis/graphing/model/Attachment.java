package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public class Attachment extends BaseGraphElement implements ISized
{
    public enum ELocation { North, East, South, West }

    private final double _width;
    private final double _height;
    private final ELocation _location;

    public Attachment(Graph g, String id, double width, double height, ELocation location)
    {
        super(g, id);
        _width = width;
        _height = height;
        _location = location;
    }

    @Override
    public Style retrieveStyle()
    {
        Style mergedStyle = _graph.getAttachmentBaseStyle().getCopy();

        if (_style != null)
            mergedStyle.mergeWith(_style);

        return mergedStyle;
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
}
