package de.itemis.graphstreamwrapper.layout.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InternalNode
{
    private String _id;
    private HashMap<String, InternalNode> _targetNodes = new HashMap<String, InternalNode>();
    private LinkedList<InternalNode> _sourceNodes = new LinkedList<InternalNode>();

    private Double _requiredSpace = null;
    private Double _x = null;
    private Double _y = null;

    public InternalNode(String id)
    {
        _id = id;
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

    public List<InternalNode> getTargets()
    {
        ArrayList<InternalNode> targets = new ArrayList<InternalNode>();
        for (InternalNode n : _targetNodes.values())
        {
            targets.add(n);
        }
        return targets;
    }

    public List<InternalNode> getSources() {
        ArrayList<InternalNode> sources = new ArrayList<InternalNode>();
        for (InternalNode n : _sourceNodes)
        {
            sources.add(n);
        }
        return sources;
    }

    // Layouting
    // --------------------------------------

    public void reset()
    {
        _x = null;
        _y = null;
    }

    public boolean isPlaced()
    {
        return _x != null && _y != null;
    }

    public void place(double x, double y)
    {
        // do not place if already placed
        if (_x != null || _y != null)
            return;

        _x = x;
        _y = y;
    }

//    public void pushLevels(double gapBetweenLevels)
//    {
//        double extraShift = gapBetweenLevels + getHeight() / 2;
//
//        double newY = _y + extraShift;
//        for (InternalNode source : getSources())
//        {
//            if (!source.isPlaced())
//                continue;
//
//            newY = Double.min(newY, source.getY());
//        }
//        newY = newY - extraShift;
//
//        _y = newY;
//    }

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
        return 0.4;
    }

    public double getHeight()
    {
        return 0.4;
    }
}
