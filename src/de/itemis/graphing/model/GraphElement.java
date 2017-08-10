package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

import java.util.Stack;

public abstract class GraphElement implements IStyled
{
    protected final Graph _graph;
    protected final String _id;

    private Object _userObject = null;
    private String _label = null;
    private boolean _isLowPioLabel = false;

    private Stack<Style> _highlightStyles = new Stack<>();
    private boolean _isSelectable = true;
    private boolean _isSelected = false;
    private boolean _isClicked = false;

    // must be set in constructors of concrete graph elements
    protected Style _styleRegular;
    protected Style _styleClicked;
    protected Style _styleSelected;

    public GraphElement(Graph g, String id)
    {
        _graph = g;
        _id = id;
        setInitialStyles();
    }

    protected abstract void setInitialStyles();

    public Graph getGraph()
    {
        return _graph;
    }

    public String getId()
    {
        return _id;
    }

    public Object getUserObject()
    {
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

    public boolean isLowPrioLabel()
    {
        return _isLowPioLabel;
    }

    public void setIsLowPrioLabel(boolean isLowPrio)
    {
        _isLowPioLabel = isLowPrio;
        _graph.labelPriorityChanged(this);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // click and selection state handling

    public boolean isSelectable()
    {
        return _isSelectable;
    }

    public void setIsSelectable(boolean selectable)
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

    protected void styleChanged()
    {
        _graph.styleChanged(this);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IStyle

    @Override
    public Style getStyleRegular()
    {
        return _styleRegular.getCopy();
    }

    @Override
    public Style getStyleClicked()
    {
        return _styleClicked.getCopy();
    }

    @Override
    public Style getStyleSelected()
    {
        return _styleSelected.getCopy();
    }

    @Override
    public void setStyleRegular(Style newStyle)
    {
        _styleRegular = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void setStyleClicked(Style newStyle)
    {
        _styleClicked = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void setStyleSelected(Style newStyle)
    {
        _styleSelected = newStyle.getCopy();
        styleChanged();
    }

    @Override
    public void pushHighlighting(Style hightlightingStyle)
    {
        _highlightStyles.push(hightlightingStyle);
        styleChanged();
    }

    @Override
    public void popHighlighting()
    {
        _highlightStyles.pop();
        styleChanged();
    }

    @Override
    public Style getActiveStyle()
    {
        if (_isClicked)
            return _styleClicked;
        else if (_isSelected)
            return _styleSelected;
        else if (_highlightStyles.size() > 0)
            return _highlightStyles.peek();
        else
            return _styleRegular;
    }

    @Override
    public String toString()
    {
        return getId();
    }
}
