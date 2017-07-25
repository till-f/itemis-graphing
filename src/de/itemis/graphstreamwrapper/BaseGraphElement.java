package de.itemis.graphstreamwrapper;

public abstract class BaseGraphElement
{
    private final String _id;

    private Object _userObject = null;
    private String _label = null;
    private String _styleId = null;

    public BaseGraphElement(String id)
    {
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
