package de.itemis.graphstreamwrapper;

import org.graphstream.ui.geom.Point2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InternalNode
{
    private String _id;
    private HashMap<String, InternalNode> _targetNodes = new HashMap<String, InternalNode>();

    private Double _requiredSpace = null;
    private Double _x = null;
    private Double _y = null;

    public InternalNode(String id)
    {
        _id = id;
    }

    public void removeTarget(String id)
    {
        _targetNodes.remove(id);
    }

    public void addTarget(InternalNode n)
    {
        _targetNodes.put(n.getID(), n);
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
