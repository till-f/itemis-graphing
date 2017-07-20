package de.itemis.graphstreamwrapper;

import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

import java.util.HashMap;
import java.util.LinkedList;

public class TreeLayout extends PipeBase implements Layout
{
    protected boolean _isLayouted = false;
    protected long _lastComputeTime = 0;

    protected HashMap<String, InternalNode> _nodeIDToNodeMap = new HashMap<String, InternalNode>();
    protected HashMap<String, LinkedList<InternalNode>> _nodeIDToParentNodesMap = new HashMap<String, LinkedList<InternalNode>>();
    protected HashMap<String, InternalNode[]> _edgeIDToNodesMap = new HashMap<String, InternalNode[]>();

    protected final DefaultConfiguration<InternalNode> _layouterConfiguration;
    protected final InternalNodeExtendProvider _nodeExtentProvider;

    protected double _gapBetweenNodes;

    public TreeLayout()
    {
        this(0.5, 0.5);
    }

    public TreeLayout(double gapBetweenLevels, double gapBetweenNodes)
    {
        _gapBetweenNodes = gapBetweenNodes;
        _layouterConfiguration = new DefaultConfiguration<InternalNode>(gapBetweenLevels, gapBetweenNodes);
        _nodeExtentProvider = new InternalNodeExtendProvider();
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
        _nodeIDToNodeMap.put(nodeId, new InternalNode(nodeId));
        _nodeIDToParentNodesMap.put(nodeId, new LinkedList<InternalNode>());
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
        if (_nodeIDToParentNodesMap.get(nodeId) != null)
        {
            for(InternalNode n : _nodeIDToParentNodesMap.get(nodeId))
            {
                n.removeTarget(nodeId);
            }
        }
        _nodeIDToNodeMap.remove(nodeId);
        _nodeIDToParentNodesMap.remove(nodeId);
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
        _nodeIDToParentNodesMap.get(toId).add(fromToArray[0]);
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId)
    {
        InternalNode[] fromToArray = _edgeIDToNodesMap.get(edgeId);
        _isLayouted = false;
        _edgeIDToNodesMap.remove(edgeId);
        _nodeIDToParentNodesMap.get(fromToArray[1]).remove(fromToArray[0]);
    }

    public void graphCleared(String sourceId, long timeId)
    {
        _isLayouted = false;
        clear();
    }

    protected LinkedList<InternalNode> getRootNodes()
    {
        LinkedList<InternalNode> rootNodes = new LinkedList<InternalNode>();
        for (String key : _nodeIDToParentNodesMap.keySet())
        {
            if (_nodeIDToParentNodesMap.get(key).size() < 1)
            {
                rootNodes.add(_nodeIDToNodeMap.get(key));
            }
        }
        return rootNodes;
    }

    @Override
    public void compute()
    {
        resetLayout();

        computeAndPublishLayout();

        publishLayout();

        _isLayouted = true;
        _lastComputeTime = System.currentTimeMillis();
    }

    private void resetLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            n.reset();
        }
    }

    private void computeAndPublishLayout()
    {
        double offset = 0;
        for (InternalNode root : getRootNodes())
        {
            DefaultTreeForTreeLayout<InternalNode> tree = new DefaultTreeForTreeLayout<InternalNode>(root);
            fillAbegoTree_recursive(tree, root);

            org.abego.treelayout.TreeLayout<InternalNode> treeLayout = new org.abego.treelayout.TreeLayout<InternalNode>(tree, _nodeExtentProvider, _layouterConfiguration);
            for(InternalNode n : treeLayout.getNodeBounds().keySet())
            {
                n.place(treeLayout.getNodeBounds().get(n).x + offset, treeLayout.getNodeBounds().get(n).y * -1);
            }
            offset += treeLayout.getBounds().getWidth() + _gapBetweenNodes;
        }
    }

    private void fillAbegoTree_recursive(DefaultTreeForTreeLayout<InternalNode> tree, InternalNode parent)
    {
        for(InternalNode child : parent.getTargets())
        {
            if (tree.hasNode(child))
                continue;

            tree.addChildren(parent, child);
            fillAbegoTree_recursive(tree, child);
        }
    }

    private void publishLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            sendNodeAttributeChanged(sourceId, n.getID(), "xyz", null,
                    new double[] { n.getX(), n.getY(), 0 });
        }
    }

    @Override
    public String getLayoutAlgorithmName()
    {
        return "Tree Layout";
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
        _nodeIDToParentNodesMap.clear();
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

    private void printDummyInfo(InternalNode n, int indent)
    {
        String indentStr = "";
        for(int i = 0; i< indent; i++)
        {
            indentStr += "  ";
        }
        System.out.println(indentStr + n.getID());

        indent++;
        for (InternalNode target : n.getTargets())
        {
            printDummyInfo(target, indent);
        }
        indent--;
    }
}
