package de.itemis.graphing.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Vertex extends GraphElement implements ISized
{
    private final Size _size;

    private final LinkedHashMap<String, Attachment> _attachments = new LinkedHashMap<String, Attachment>();

    private LinkedHashSet<Edge> _outgoingEdges = new LinkedHashSet<Edge>();
    private LinkedHashSet<Edge> _incomingEdges = new LinkedHashSet<Edge>();

    private Double _x = null;
    private Double _y = null;

    public Vertex(Graph g, String id, Size size)
    {
        super(g, id);
        _size = size;
        setStyle(EStyle.Regular, g.getDefaultVertexStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, g.getDefaultVertexStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, g.getDefaultVertexStyle(EStyle.Selected));
    }

    // -----------------------------------------------------------------------------------------------------------------
    // edges and relations to other vertexes

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

    public Set<Edge> getOutgoingEdges()
    {
        return _outgoingEdges;
    }

    public Set<Edge> getIncomingEdges()
    {
        return _incomingEdges;
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

    // -----------------------------------------------------------------------------------------------------------------
    // attachments and calculated size

    public Attachment addAttachment(String id, double width, double height, Attachment.ELocation location)
    {
        return addAttachment(id, width, height, 0.0, location, false);
    }

    public Attachment addAttachment(String id, double width, double height, double padding, Attachment.ELocation location)
    {
        return addAttachment(id, width, height, padding, location, false);
    }

    public Attachment addAttachment(String id, double width, double height, Attachment.ELocation location, boolean affectDynamicLayout)
    {
        return addAttachment(id, width, height, 0.0, location, affectDynamicLayout);
    }

    public Attachment addAttachment(String id, double width, double height, double padding, Attachment.ELocation location, boolean affectDynamicLayout)
    {
        Attachment a = new Attachment(this, id, new Size(width, height), padding, location, affectDynamicLayout);
        _attachments.put(id, a);

        _graph.attachmentAdded(a);

        styleChanged();

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

        styleChanged();
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
    public Size getInnerSize()
    {
        return _size;
    }

    @Override
    public Size getOuterSize()
    {
        // width
        double maxWidthEast = 0;
        double maxWidthWest = 0;
        for(Attachment a : _attachments.values())
        {
            if (a.getLocation() == Attachment.ELocation.East)
                maxWidthEast = Math.max(maxWidthEast, a.getOuterSize().getWidth());
            if (a.getLocation() == Attachment.ELocation.West)
                maxWidthWest = Math.max(maxWidthWest, a.getOuterSize().getWidth());
        }
        double maxWidthNorthSouth = Math.max(getAttachmentsStackSpace(Attachment.ELocation.North), getAttachmentsStackSpace(Attachment.ELocation.South));
        double width = Math.max(maxWidthNorthSouth, _size.getWidth() + maxWidthEast + maxWidthWest);

        // height
        double maxHeightNorth = 0;
        double maxHeightSouth = 0;
        for(Attachment a : _attachments.values())
        {
            if (a.getLocation() == Attachment.ELocation.North)
                maxHeightNorth = Math.max(maxHeightNorth, a.getOuterSize().getHeight());
            if (a.getLocation() == Attachment.ELocation.South)
                maxHeightSouth = Math.max(maxHeightSouth, a.getOuterSize().getHeight());
        }
        double maxHeightEastWest = Math.max(getAttachmentsStackSpace(Attachment.ELocation.East), getAttachmentsStackSpace(Attachment.ELocation.West));
        double height = Math.max(maxHeightEastWest, _size.getHeight() + maxHeightNorth + maxHeightSouth);

        return new Size(width, height);
    }

    public double getAttachmentsStackSpace(Attachment.ELocation location)
    {
        double space = 0;
        Attachment previousAttachment = null;
        for(Attachment attachment : _attachments.values())
        {
            if (attachment.getLocation() != location)
                continue;

            if (previousAttachment != null)
                space -= Math.min(attachment.getPadding(), previousAttachment.getPadding());

            if (location == Attachment.ELocation.North || location == Attachment.ELocation.South)
            {
                space += attachment.getOuterSize().getWidth();
            }
            else
            {
                space += attachment.getOuterSize().getHeight();
            }

            previousAttachment = attachment;
        }

        return space;
    }

    public double getAttachmentsFlatSpace(Attachment.ELocation location)
    {
        double space = 0;
        for(Attachment attachment : _attachments.values())
        {
            if (attachment.getLocation() != location)
                continue;

            if (location == Attachment.ELocation.North || location == Attachment.ELocation.South)
            {
                space = Math.max(space, attachment.getOuterSize().getHeight());
            }
            else
            {
                space = Math.max(space, attachment.getOuterSize().getWidth());
            }
        }

        return space;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // placement

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

    public boolean isPlaced()
    {
        return _x != null && _y != null;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

}
