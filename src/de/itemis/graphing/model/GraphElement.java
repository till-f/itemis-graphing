package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public abstract class GraphElement implements IStyled
{
    protected final Graph _graph;
    protected final String _id;

    private Object _userObject = null;
    private String _label = null;

    private Style[] _styles = new Style[EStyle.values().length];
    private boolean _isSelectable = true;
    private boolean _isSelected = false;
    private boolean _isClicked = false;
    private boolean _isHighlighted = false;

    public GraphElement(Graph g, String id)
    {
        _graph = g;
        _id = id;
    }

    public Graph getGraph()
    {
        return _graph;
    }

    public String getId() {
        return _id;
    }

    public Object getUserObject() {
        return _userObject;
    }

    public void setUserObject(Object userObject)
    {
        _userObject = userObject;
    }

    public String getLabel()
    {
        return _label;
    }

    public void setLabel(String label)
    {
        _label = label;
        _graph.labelChanged(this);

    }

    public boolean isSelectable()
    {
        return _isSelectable;
    }

    public void setSelectable(boolean selectable)
    {
        _isSelectable = selectable;
    }

    public boolean isSelected()
    {
        return _isSelected;
    }

    public void setSelected(boolean selected)
    {
        if (_isSelected != selected)
        {
            _isSelected = selected;
            styleChanged();
        }
    }

    public void clickBegin()
    {
        _isClicked = true;
        styleChanged();
    }

    public void clickEnd()
    {
        _isClicked = false;
        styleChanged();
    }

    public void styleChanged()
    {
        _graph.styleChanged(this);
    }


    @Override
    public Style getStyle()
    {
        return _styles[EStyle.Regular.ordinal()].getCopy();
    }

    @Override
    public void setStyle(Style newStyle)
    {
        setStyle(EStyle.Regular, newStyle);
    }

    @Override
    public void setStyle(EStyle styleSelector, Style newStyle)
    {
        _styles[styleSelector.ordinal()] = newStyle.getCopy();
        _styles[styleSelector.ordinal()].setParent(this);

        styleChanged();
    }

    @Override
    public void beginHighlight(Style hightlightingStyle)
    {
        _isHighlighted = true;
        setStyle(EStyle.Highlighted, hightlightingStyle);
    }

    @Override
    public void endHighlight()
    {
        _isHighlighted = false;
        styleChanged();
    }

    @Override
    public Style getActiveStyle()
    {
        if (_isClicked)
            return _styles[EStyle.Clicked.ordinal()].getCopy();
        else if (_isSelected)
            return _styles[EStyle.Selected.ordinal()].getCopy();
        else if (_isHighlighted)
            return _styles[EStyle.Highlighted.ordinal()].getCopy();
        else
            return _styles[EStyle.Regular.ordinal()].getCopy();
    }

    @Override
    public String toString()
    {
        return getId();
    }
}
