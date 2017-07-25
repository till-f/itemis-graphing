package de.itemis.graphstreamwrapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Vertex extends BaseGraphElement implements IAttachmentContainer
{
    private final double _width;
    private final double _height;

    private final LinkedList<Attachment> _attachments = new LinkedList<>();

    private HashSet<Edge> _outgoingEdges = new HashSet<Edge>();
    private HashSet<Edge> _incomingEdges = new HashSet<Edge>();

    private Double _x = null;
    private Double _y = null;

    public Vertex(String id, double width, double height)
    {
        super(id);
        _width = width;
        _height = height;
    }

    public void addOutgoingEdge(Edge e)
    {
        _outgoingEdges.add(e);
    }

    public void removeOutgoingEdge(Edge e)
    {
        _outgoingEdges.remove(e);
    }

    public void addIncomingEdge(Edge e)
    {
        _incomingEdges.add(e);
    }

    public void removeIncomingEdge(Edge e)
    {
        _incomingEdges.remove(e);
    }

    public double getWidth()
    {
        return _width;
    }

    public double getHeight()
    {
        return _height;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

    public List<Vertex> getTargets()
    {
        LinkedList<Vertex> targets = new LinkedList<Vertex>();
        for (Edge e : _outgoingEdges)
        {
            targets.add(e.getTo());
        }
        return targets;
    }

    public List<Vertex> getSources()
    {
        LinkedList<Vertex> sources = new LinkedList<Vertex>();
        for (Edge e : _incomingEdges)
        {
            sources.add(e.getFrom());
        }
        return sources;
    }

    public void reset()
    {
        _x = null;
        _y = null;
    }

    public void place(double x, double y, boolean force)
    {
        if (!force && _x != null && _y != null)
        {
            return;
        }

        _x = x;
        _y = y;
    }

    public List<Edge> removeEdgesTo(Vertex target)
    {
        LinkedList<Edge> toRemove = new LinkedList<Edge>();
        for (Edge e : _outgoingEdges)
        {
            if (e.getTo() == target)
                toRemove.add(e);
        }
        _outgoingEdges.removeAll(toRemove);

        return toRemove;
    }

    public List<Edge> removeEdgesFrom(Vertex source)
    {
        LinkedList<Edge> toRemove = new LinkedList<Edge>();
        for (Edge e : _incomingEdges)
        {
            if (e.getFrom() == source)
                toRemove.add(e);
        }
        _incomingEdges.removeAll(toRemove);

        return toRemove;
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
