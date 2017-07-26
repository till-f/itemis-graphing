package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public class Attachment extends BaseGraphElement implements ISized
{
    private final double _width;
    private final double _height;

    public Attachment(Graph g, String id, double width, double height)
    {
        super(g, id);
        _width = width;
        _height = height;
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
    public double getWidth()
    {
        return _width;
    }

    @Override
    public double getHeight()
    {
        return _height;
    }
}
