package de.itemis.graphstreamwrapper;

public class InternalEdge
{
    private final String _id;
    private final InternalNode _from;
    private final InternalNode _to;
    private final Object _userObject;

    public InternalEdge(String id, InternalNode from, InternalNode to, Object object)
    {
        _id = id;
        _from = from;
        _to = to;
        _userObject = object;
    }

    public String getId() {
        return _id;
    }

    public InternalNode getFrom() {
        return _from;
    }

    public InternalNode getTo() {
        return _to;
    }

    public Object getUserObject() {
        return _userObject;
    }
}
