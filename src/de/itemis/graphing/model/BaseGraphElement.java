package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

public abstract class BaseGraphElement implements IStyled
{
    protected final Graph _graph;
    protected final String _id;

    private Object _userObject = null;
    private String _label = null;

    public BaseGraphElement(Graph g, String id)
    {
        _graph = g;
        _id = id;
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
        System.out.println("INFO: style changed");
    }
}
