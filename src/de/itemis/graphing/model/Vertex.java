package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Vertex extends BaseGraphElement implements IAttachmentContainer, ISized
{
    private final double _width;
    private final double _height;

    private final LinkedList<Attachment> _attachments = new LinkedList<>();

    private HashSet<Edge> _outgoingEdges = new HashSet<Edge>();
    private HashSet<Edge> _incomingEdges = new HashSet<Edge>();

    private Double _x = null;
    private Double _y = null;

    public Vertex(Graph g, String id, double width, double height)
    {
        super(g, id);
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

    @Override
    public double getWidth()
    {
        return _width;
    }

    @Override
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
    public Style retrieveStyle()
    {
        Style mergedStyle = _graph.getVertexBaseStyle().getCopy();

        if (_style != null)
            mergedStyle.mergeWith(_style);

        return mergedStyle;
    }

    @Override
    public Attachment addAttachment(String id, double width, double height) {
        Attachment a = new Attachment(_graph, id, width, height);
        _attachments.add(a);
        return a;
    }

    public List<Attachment> getAttachments() {
        return _attachments;
    }
}
