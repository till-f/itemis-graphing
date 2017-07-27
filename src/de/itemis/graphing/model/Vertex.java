package de.itemis.graphing.model;

import java.util.*;

public class Vertex extends BaseGraphElement implements ISized
{
    private final double _width;
    private final double _height;

    private final HashMap<String, Attachment> _attachments = new HashMap<String, Attachment>();

    private HashSet<Edge> _outgoingEdges = new HashSet<Edge>();
    private HashSet<Edge> _incomingEdges = new HashSet<Edge>();

    private Double _x = null;
    private Double _y = null;

    public Vertex(Graph g, String id, double width, double height)
    {
        super(g, id);
        _width = width;
        _height = height;
        setStyle(EStyle.Regular, g.getDefaultVertexStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, g.getDefaultVertexStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, g.getDefaultVertexStyle(EStyle.Selected));
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

    public Attachment addAttachment(String id, double width, double height, Attachment.ELocation location)
    {
        Attachment a = new Attachment(this, id, width, height, location);
        _attachments.put(id, a);

        _graph.attachmentAdded(a);

        return a;
    }

    public void removeAttachment(String id)
    {
        Attachment a = _attachments.get(id);

        if (a != null)
        {
            _attachments.remove(id);
            _graph.attachmentRemoved(a);
        }
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

    public List<Attachment> getAttachments()
    {
        LinkedList<Attachment> attachments = new LinkedList<>();
        for (Attachment a : _attachments.values())
        {
            attachments.add(a);
        }
        return attachments;
    }

    @Override
    public double getBaseWidth()
    {
        return _width;
    }

    @Override
    public double getBaseHeight()
    {
        return _height;
    }

    @Override
    public double getFinalWidth()
    {
        double maxWidthEast = 0;
        double maxWidthWest = 0;
        for(Attachment a : _attachments.values())
        {
            if (a.getLocation() == Attachment.ELocation.East)
                maxWidthEast = Math.max(maxWidthEast, a.getFinalWidth());
            if (a.getLocation() == Attachment.ELocation.West)
                maxWidthWest = Math.max(maxWidthWest, a.getFinalWidth());
        }
        double maxWidthNorthSouth = Math.max(getAttachmentsSpace(Attachment.ELocation.North), getAttachmentsSpace(Attachment.ELocation.South));
        return Math.max(maxWidthNorthSouth, _width + maxWidthEast + maxWidthWest);
    }

    @Override
    public double getFinalHeight()
    {
        double maxHeightNorth = 0;
        double maxHeightSouth = 0;
        for(Attachment a : _attachments.values())
        {
            if (a.getLocation() == Attachment.ELocation.North)
                maxHeightNorth = Math.max(maxHeightNorth, a.getFinalHeight());
            if (a.getLocation() == Attachment.ELocation.South)
                maxHeightSouth = Math.max(maxHeightSouth, a.getFinalHeight());
        }
        double maxHeightEastWest = Math.max(getAttachmentsSpace(Attachment.ELocation.East), getAttachmentsSpace(Attachment.ELocation.West));
        return Math.max(maxHeightEastWest, _height + maxHeightNorth + maxHeightSouth);
    }

    public double getAttachmentsSpace(Attachment.ELocation location)
    {
        double space = 0;
        for(Attachment a : _attachments.values())
        {
            if (a.getLocation() != location)
                continue;

            if (location == Attachment.ELocation.North || location == Attachment.ELocation.South)
            {
                space += a.getFinalWidth();
            }
            else
            {
                space += a.getFinalHeight();
            }
        }
        return space;
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

    public Set<Edge> getOutgoingEdges()
    {
        return _outgoingEdges;
    }

    public Set<Edge> getIncomingEdges()
    {
        return _incomingEdges;
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
}
