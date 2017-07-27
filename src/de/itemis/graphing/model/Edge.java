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

    public Edge(Graph g, String id, Vertex from, Vertex to)
    {
        super(g, id);
        _from = from;
        _to = to;
        setSelectable(false);
        setStyle(EStyle.Regular, g.getDefaultEdgeStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, g.getDefaultEdgeStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, g.getDefaultEdgeStyle(EStyle.Selected));
    }

    public Edge(Graph g, String id, String fromId, String toId)
    {
        super(g, id);
        _fromId = fromId;
        _toId = toId;
        setSelectable(false);
        setStyle(EStyle.Regular, g.getDefaultEdgeStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, g.getDefaultEdgeStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, g.getDefaultEdgeStyle(EStyle.Selected));
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
}
