package de.itemis.graphstreamwrapper.layout;

import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InternalNode
{
    private String _id;
    private Double _width = null;
    private Double _height = null;

    private HashMap<String, InternalNode> _targetNodes = new HashMap<String, InternalNode>();
    private LinkedList<InternalNode> _sourceNodes = new LinkedList<InternalNode>();

    private Double _x = null;
    private Double _y = null;

    public InternalNode(Node node)
    {
        _id = node.getId();
        _width = node.getAttribute("ui.width");
        _height = node.getAttribute("ui.height");
    }

    public void addTarget(InternalNode n)
    {
        _targetNodes.put(n.getID(), n);
    }

    public void removeTarget(String id)
    {
        _targetNodes.remove(id);
    }

    public void addSource(InternalNode n)
    {
        _sourceNodes.add(n);
    }

    public void removeSource(InternalNode n)
    {
        _sourceNodes.remove(n);
    }

    public String getID()
    {
        return _id;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

    public double getWidth()
    {
        return _width;
    }

    public double getHeight()
    {
        return _height;
    }

    public List<InternalNode> getTargets()
    {
        ArrayList<InternalNode> targets = new ArrayList<InternalNode>();
        for (InternalNode n : _targetNodes.values())
        {
            targets.add(n);
        }
        return targets;
    }

    public List<InternalNode> getSources()
    {
        ArrayList<InternalNode> sources = new ArrayList<InternalNode>();
        for (InternalNode n : _sourceNodes)
        {
            sources.add(n);
        }
        return sources;
    }

    public void reset()
    {
        _x = null;
        _y = null;
    }

    public void place(double x, double y)
    {
        // do not place if already placed
        if (_x != null || _y != null)
            return;

        _x = x;
        _y = y;
    }
}
