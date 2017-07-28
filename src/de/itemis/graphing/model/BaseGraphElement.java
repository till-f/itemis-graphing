package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public abstract class BaseGraphElement implements IStyled
{
    protected final Graph _graph;
    protected final String _id;

    private Object _userObject = null;
    private String _label = null;

    private Style[] _styles = new Style[3];
    private EStyle _activeStyle = EStyle.Regular;
    private boolean _isSelectable = true;

    public BaseGraphElement(Graph g, String id)
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
    }

    public void styleChanged()
    {
        _graph.styleChanged(this);
    }

    public boolean isSelectable()
    {
        return _isSelectable;
    }

    public void setSelectable(boolean selectable)
    {
        _isSelectable = selectable;
    }

    @Override
    public Style getStyle()
    {
        return getStyle(EStyle.Regular);
    }

    @Override
    public void setStyle(Style newStyle)
    {
        setStyle(EStyle.Regular, newStyle);
    }

    @Override
    public Style getStyle(EStyle styleSelecgtor)
    {
        return _styles[styleSelecgtor.ordinal()];
    }

    @Override
    public void setStyle(EStyle styleSelector, Style newStyle)
    {
        _styles[styleSelector.ordinal()] = newStyle.getCopy();
        _styles[styleSelector.ordinal()].setParent(this);

        // adapt clicked/selected style to new regular style (user may set them explicitely to override this)
        if (styleSelector == EStyle.Regular)
        {
            double thickness = (this instanceof Edge) ? Graph.DEFAULT_HL_LINE_THICKNESS_EDGE : Graph.DEFAULT_HL_LINE_THICKNESS;

            Style clicked = newStyle.getCopy();
            clicked.setLineThickness(thickness);
            clicked.setLineColor(Graph.DEFAULT_HL_LINE_COLOR);
            setStyle(EStyle.Clicked, clicked);

            Style selected = newStyle.getCopy();
            selected.setLineThickness(thickness);
            selected.setLineColor(Graph.DEFAULT_HL_LINE_COLOR);
            setStyle(EStyle.Selected, selected);
        }

        if (styleSelector == _activeStyle)
            styleChanged();
    }

    @Override
    public void selectActiveStyle(EStyle styleSelector)
    {
        _activeStyle = styleSelector;
        styleChanged();
    }

    @Override
    public Style getActiveStyle()
    {
        return _styles[_activeStyle.ordinal()];
    }


}
