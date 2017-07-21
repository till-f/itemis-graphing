package de.itemis.graphstreamwrapper.layout;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class StaticLayout extends PipeBase implements Layout
{
    protected final Graph _originalGraph;

    protected boolean _isLayouted = false;
    protected long _lastComputeTime = 0;

    protected HashMap<String, InternalNode> _nodeIDToNodeMap = new HashMap<String, InternalNode>();
    protected HashMap<String, InternalNode[]> _edgeIDToNodesMap = new HashMap<String, InternalNode[]>();

    public StaticLayout(Graph originalGraph)
    {
        _originalGraph = originalGraph;
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
        Node n = _originalGraph.getNode(nodeId);
        InternalNode newNode = new InternalNode(n);
        _nodeIDToNodeMap.put(nodeId, newNode);
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
        if (_nodeIDToNodeMap.get(nodeId) != null)
        {
            for(InternalNode n : _nodeIDToNodeMap.get(nodeId).getSources())
            {
                n.removeTarget(nodeId);
            }
        }
        _nodeIDToNodeMap.remove(nodeId);
    }

    public void edgeAdded(String sourceId, long timeId, String edgeId,
                          String fromId, String toId, boolean directed)
    {
        if (!_nodeIDToNodeMap.containsKey(fromId))
            throw new IllegalArgumentException("Edge from unknown node added"); // hopefully framework calls this in correct order...

        if (!_nodeIDToNodeMap.containsKey(toId))
            throw new IllegalArgumentException("Edge to unknown node added"); // hopefully framework calls this in correct order...

        _isLayouted = false;

        InternalNode[] fromToArray = new InternalNode[2];
        fromToArray[0] = _nodeIDToNodeMap.get(fromId);
        fromToArray[1] = _nodeIDToNodeMap.get(toId);
        _edgeIDToNodesMap.put(edgeId, fromToArray);

        fromToArray[0].addTarget(fromToArray[1]);
        _nodeIDToNodeMap.get(toId).addSource(fromToArray[0]);
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId)
    {
        _isLayouted = false;
        InternalNode[] fromToArray = _edgeIDToNodesMap.get(edgeId);
        _edgeIDToNodesMap.remove(edgeId);
        _nodeIDToNodeMap.get(fromToArray[1]).removeSource(fromToArray[0]);
    }

    public void graphCleared(String sourceId, long timeId)
    {
        _isLayouted = false;
        clear();
    }

    @Override
    public void compute()
    {
        resetLayout();

        computeLayout();

        publishLayout();

        _isLayouted = true;
        _lastComputeTime = System.currentTimeMillis();
    }

    @Override
    public int getNodeMovedCount()
    {
        return 0;
    }

    @Override
    public double getStabilization()
    {
        return _isLayouted ? 1 : 0;
    }

    @Override
    public double getStabilizationLimit()
    {
        return 0.9;
    }

    @Override
    public Point3 getLowPoint()
    {
        throw new UnsupportedOperationException("deprecated ?!");
    }

    @Override
    public Point3 getHiPoint()
    {
        throw new UnsupportedOperationException("deprecated ?!");
    }

    @Override
    public int getSteps()
    {
        return 0;
    }

    @Override
    public long getLastStepTime()
    {
        return 0;
    }

    @Override
    public double getQuality()
    {
        return 0;
    }

    @Override
    public double getForce()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        _edgeIDToNodesMap.clear();
        _nodeIDToNodeMap.clear();
    }

    @Override
    public void setForce(double value)
    {
        // not supported
    }

    @Override
    public void setStabilizationLimit(double value)
    {
        // not supported
    }

    @Override
    public void setQuality(double qualityLevel)
    {
        // not supported
    }

    @Override
    public void setSendNodeInfos(boolean send)
    {
        // not supported
    }

    @Override
    public void shake()
    {
        // not supported
    }

    @Override
    public void moveNode(String id, double x, double y, double z)
    {
        // not supported
    }

    @Override
    public void freezeNode(String id, boolean frozen)
    {
        // not supported
    }

    protected LinkedList<InternalNode> getRootNodes()
    {
        LinkedList<InternalNode> rootNodes = new LinkedList<InternalNode>();
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            if (n.getSources().size() < 1)
            {
                rootNodes.add(n);
            }
        }
        return rootNodes;
    }

    protected void resetLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            n.reset();
        }
    }

    protected void publishLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            sendNodeAttributeChanged(sourceId, n.getID(), "xyz", null,
                    new double[] { n.getX(), n.getY(), 0 });
        }
    }

    protected abstract void computeLayout();

}
