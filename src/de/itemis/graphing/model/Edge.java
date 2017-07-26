package de.itemis.graphing.model;

import java.util.LinkedList;
import java.util.List;

public class Edge extends BaseGraphElement implements IAttachmentContainer
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
    }

    public Edge(Graph g, String id, String fromId, String toId)
    {
        super(g, id);
        _fromId = fromId;
        _toId = toId;
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
    public Attachment addAttachment(String id, double radius, double degree) {
        Attachment a = new Attachment(_graph, id, radius, degree);
        _attachments.add(a);
        return a;
    }

    public List<Attachment> getAttachments() {
        return _attachments;
    }
}
