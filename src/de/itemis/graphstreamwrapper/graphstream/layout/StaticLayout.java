package de.itemis.graphstreamwrapper.graphstream.layout;

import de.itemis.graphstreamwrapper.InternalGraph;
import de.itemis.graphstreamwrapper.InternalNode;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

public abstract class StaticLayout extends PipeBase implements Layout
{
    protected final InternalGraph _internalGraph;

    protected boolean _isLayouted = false;
    protected long _lastComputeTime = 0;

    public StaticLayout(InternalGraph internalGraph)
    {
        _internalGraph = internalGraph;
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId)
    {
        _isLayouted = false;
    }

    public void edgeAdded(String sourceId, long timeId, String edgeId,
                          String fromId, String toId, boolean directed)
    {
        _isLayouted = false;
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId)
    {
        _isLayouted = false;
    }

    public void graphCleared(String sourceId, long timeId)
    {
        _isLayouted = false;
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
        throw new UnsupportedOperationException("not supported"); // deprecated
    }

    @Override
    public Point3 getHiPoint()
    {
        throw new UnsupportedOperationException("not supported"); // deprecated
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
        // not supported
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

    protected void resetLayout()
    {
        for (InternalNode n : _internalGraph.getNodes())
        {
            n.reset();
        }
    }

    protected void publishLayout()
    {
        for (InternalNode n : _internalGraph.getNodes())
        {
            sendNodeAttributeChanged(sourceId, n.getId(), "xyz", null,
                    new double[] { n.getX(), n.getY(), 0 });
        }
    }

    protected abstract void computeLayout();

}