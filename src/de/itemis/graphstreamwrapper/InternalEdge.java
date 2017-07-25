package de.itemis.graphstreamwrapper;

import java.util.LinkedList;
import java.util.List;

public class InternalEdge extends BaseGraphElement implements IAttachmentContainer
{
    private final LinkedList<Attachment> _attachments = new LinkedList<>();

    private InternalNode _from;
    private InternalNode _to;

    private String _fromId;
    private String _toId;

    public InternalEdge(String id, InternalNode from, InternalNode to)
    {
        super(id);
        _from = from;
        _to = to;
    }

    public InternalEdge(String id, String fromId, String toId)
    {
        super(id);
        _fromId = fromId;
        _toId = toId;
    }

    public InternalNode getFrom() {
        return _from;
    }

    public InternalNode getTo() {
        return _to;
    }

    public String getFromId() {
        return _fromId;
    }

    public String getToId() {
        return _toId;
    }

    public void setFromTo(InternalNode from, InternalNode to)
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
        Attachment a = new Attachment(id, radius, degree);
        _attachments.add(a);
        return a;
    }

    public List<Attachment> getAttachments() {
        return _attachments;
    }
}
