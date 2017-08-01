package de.itemis.graphing.view.graphstream.layout;

import de.itemis.graphing.layout.ILayout;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

public class StaticLayout extends PipeBase implements Layout
{
    protected final Object _graphLock = new Object();

    protected final Graph _graph;
    protected final ILayout _layout;

    protected boolean _isLayouted = false;
    protected long _lastComputeTime = 0;

    public StaticLayout(Graph graph, ILayout layout)
    {
        _graph = graph;
        _layout = layout;
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId)
    {
        synchronized (_graphLock)
        {
            _isLayouted = false;
        }
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId)
    {
        synchronized (_graphLock)
        {
            _isLayouted = false;
        }
    }

    public void edgeAdded(String sourceId, long timeId, String edgeId,
                          String fromId, String toId, boolean directed)
    {
        synchronized (_graphLock)
        {
            _isLayouted = false;
        }
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId)
    {
        synchronized (_graphLock)
        {
            _isLayouted = false;
        }
    }

    public void graphCleared(String sourceId, long timeId)
    {
        synchronized (_graphLock)
        {
            _isLayouted = false;
        }
    }

    @Override
    public void compute()
    {
        synchronized (_graphLock)
        {
            resetLayout();

            computeLayout();

            publishLayout();

            _isLayouted = true;
            _lastComputeTime = System.currentTimeMillis();
        }
    }

    @Override
    public String getLayoutAlgorithmName()
    {
        return "Static ILayout using Layouter " + _layout.getClass();
    }

    @Override
    public int getNodeMovedCount()
    {
        return 0;
    }

    @Override
    public double getStabilization()
    {
        synchronized (_graphLock)
        {
            return _isLayouted ? 1 : 0;
        }
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
        for (Vertex n : _graph.getVertexes())
        {
            n.reset();
        }
    }

    protected void publishLayout()
    {
        for (Vertex n : _graph.getVertexes())
        {
            sendNodeAttributeChanged(sourceId, n.getId(), "xyz", null,
                    new double[] { n.getX(), n.getY(), 0 });
        }
    }

    protected void computeLayout()
    {
        _layout.apply(_graph);
    }

}
