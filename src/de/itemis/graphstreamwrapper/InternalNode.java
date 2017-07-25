package de.itemis.graphstreamwrapper;

import org.graphstream.graph.Node;

import java.util.*;

public class InternalNode
{
    private final String _id;
    private final double _width;
    private final double _height;
    private final Object _userObject;

    private HashSet<InternalEdge> _outgoingEdges = new HashSet<InternalEdge>();
    private HashSet<InternalEdge> _incomingEdges = new HashSet<InternalEdge>();

    private Double _x = null;
    private Double _y = null;

    public InternalNode(String id, double width, double height, Object userObject)
    {
        _id = id;
        _width = width;
        _height = height;
        _userObject = userObject;
    }

    public void addOutgoingEdge(InternalEdge e)
    {
        _outgoingEdges.add(e);
    }

    public void removeOutgoingEdge(InternalEdge e)
    {
        _outgoingEdges.remove(e);
    }

    public void addIncomingEdge(InternalEdge e)
    {
        _incomingEdges.add(e);
    }

    public void removeIncomingEdge(InternalEdge e)
    {
        _incomingEdges.remove(e);
    }

    public String getID()
    {
        return _id;
    }

    public double getWidth()
    {
        return _width;
    }

    public double getHeight()
    {
        return _height;
    }

    public Object getUserObject()
    {
        return _userObject;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

    public List<InternalNode> getTargets()
    {
        ArrayList<InternalNode> targets = new ArrayList<InternalNode>();
        for (InternalEdge e : _outgoingEdges)
        {
            targets.add(e.getTo());
        }
        return targets;
    }

    public List<InternalNode> getSources()
    {
        ArrayList<InternalNode> sources = new ArrayList<InternalNode>();
        for (InternalEdge e : _incomingEdges)
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

    public List<InternalEdge> removeEdgesTo(InternalNode target)
    {
        LinkedList<InternalEdge> toRemove = new LinkedList<InternalEdge>();
        for (InternalEdge e : _outgoingEdges)
        {
            if (e.getTo() == target)
                toRemove.add(e);
        }
        _outgoingEdges.removeAll(toRemove);

        return toRemove;
    }

    public List<InternalEdge> removeEdgesFrom(InternalNode source)
    {
        LinkedList<InternalEdge> toRemove = new LinkedList<InternalEdge>();
        for (InternalEdge e : _incomingEdges)
        {
            if (e.getFrom() == source)
                toRemove.add(e);
        }
        _incomingEdges.removeAll(toRemove);

        return toRemove;
    }
}
