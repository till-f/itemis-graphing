package de.itemis.graphing.model;

public abstract class BaseGraphElement
{
    protected final Graph _graph;
    protected final String _id;

    private Object _userObject = null;
    private String _label = null;
    private String _styleId = null;

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

    public String getStyleId()
    {
        return _styleId;
    }

    public void setStyleId(String styleId)
    {
        _styleId = styleId;
    }

}
