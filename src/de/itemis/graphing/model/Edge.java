package de.itemis.graphing.model;

import de.itemis.graphing.model.style.EdgeStyle;

import java.util.LinkedList;

public class Edge extends BaseGraphElement
{
    private final LinkedList<Attachment> _attachments = new LinkedList<>();

    private Vertex _from;
    private Vertex _to;

    private String _fromId;
    private String _toId;

    private EdgeStyle _style;

    public Edge(Graph g, String id, Vertex from, Vertex to)
    {
        super(g, id);
        _from = from;
        _to = to;
        _style = g.getDefaultEdgeStyle();
    }

    public Edge(Graph g, String id, String fromId, String toId)
    {
        super(g, id);
        _fromId = fromId;
        _toId = toId;
        _style = g.getDefaultEdgeStyle();
    }

    public Vertex getFrom() {
        return _from;
    }

    public Vertex getTo() {
        return _to;
    }

    public String getFromId() {
        return _fromId;
    }

    public String getToId() {
        return _toId;
    }

    public void setFromTo(Vertex from, Vertex to)
    {
        if (from == null && to == null)
        {
            _from = from;
            _to = to;
        }
        else
        {
            throw new IllegalArgumentException("source and target of an edge can only be set once");
        }
    }

    @Override
    public EdgeStyle getStyle()
    {
        return _style;
    }

    public void setStyle(EdgeStyle style)
    {
        _style = style;
        style.setParent(this);
        styleChanged();
    }

}
