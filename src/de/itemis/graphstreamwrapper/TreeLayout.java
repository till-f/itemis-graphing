package de.itemis.graphstreamwrapper;

import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point2;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TreeLayout extends PipeBase implements Layout
{
    protected final double HORIZONTAL_SPACE_FACTOR;
    protected final double VERTICAL_SPACE_FACTOR;

    protected boolean _isLayouted = false;
    protected long _lastComputeTime = 0;

    protected HashMap<String, InternalNode> _nodeIDToNodeMap = new HashMap<String, InternalNode>();
    protected HashMap<String, LinkedList<InternalNode>> _nodeIDToParentNodesMap = new HashMap<String, LinkedList<InternalNode>>();
    protected HashMap<String, InternalNode[]> _edgeIDToNodesMap = new HashMap<String, InternalNode[]>();

    protected class InternalNode
    {
        private String _id;
        private HashMap<String, InternalNode> _targetNodes = new HashMap<String, InternalNode>();

        private Double _requiredSpace = null;
        private Double _layoutX = null;
        private Double _layoutY = null;

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

        public double getRequiredSpace()
        {
            if (_requiredSpace != null)
                return _requiredSpace.doubleValue();

            _requiredSpace = 0.0;
            for (InternalNode target : getTargets())
            {
                _requiredSpace += target.getRequiredSpace();
            }
            if (_requiredSpace == 0.0) _requiredSpace = 1.0;

            return _requiredSpace.doubleValue();
        }

        public void computePosition(int mainOffset, double internalOffset, int level)
        {
            if (_layoutX != null && _layoutY != null)
                return;

            double correction = getRequiredSpace() / 2 - 0.5;
            _layoutX = HORIZONTAL_SPACE_FACTOR * (mainOffset + internalOffset + correction);
            _layoutY = VERTICAL_SPACE_FACTOR * level;

            double subInternalOffset = internalOffset;
            int trgtCount = getTargets().size();
            for (int idx=0; idx < trgtCount; idx++)
            {
                getTargets().get(idx).computePosition(mainOffset, subInternalOffset, level-1);
                if (idx+1 < trgtCount)
                {
                    subInternalOffset += (getTargets().get(idx).getRequiredSpace() + getTargets().get(idx+1).getRequiredSpace()) / 2;
                }
            }
        }

        public Point2 getPosition()
        {
            if (_layoutX == null || _layoutY == null)
                throw new UnsupportedOperationException("getPosition requires computePosition() to be called");

            return new Point2(_layoutX, _layoutY);
        }

        public void reset() {
            _requiredSpace = null;
            _layoutX = null;
            _layoutY = null;
        }
    }

    public TreeLayout()
    {
        HORIZONTAL_SPACE_FACTOR = 1.0;
        VERTICAL_SPACE_FACTOR = 1.0;
    }

    public TreeLayout(double horizontalSpaceFactor, double verticalSpaceFctor)
    {
        HORIZONTAL_SPACE_FACTOR = horizontalSpaceFactor;
        VERTICAL_SPACE_FACTOR = verticalSpaceFctor;
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

        computeLayout();

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

    private void computeLayout()
    {
        LinkedList<InternalNode> _rootNodes = getRootNodes();

        int mainOffset = 0;
        for (InternalNode n : _rootNodes)
        {
            n.computePosition(mainOffset, 0.0, 0);
            mainOffset += n.getRequiredSpace();
        }
    }

    private void publishLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values()) {
            sendNodeAttributeChanged(sourceId, n.getID(), "xyz", null,
                    new double[] { n.getPosition().x, n.getPosition().y, 0 });
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
        System.out.println(indentStr + n.getID() + " " + n.getRequiredSpace() + ", x=" + n.getPosition().x + "y=" + n.getPosition().y);

        indent++;
        for (InternalNode target : n.getTargets())
        {
            printDummyInfo(target, indent);
        }
        indent--;
    }
}
