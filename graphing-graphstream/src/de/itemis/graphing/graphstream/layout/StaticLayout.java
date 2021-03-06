package de.itemis.graphing.graphstream.layout;

import de.itemis.graphing.model.Coordinates;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;
import org.graphstream.stream.PipeBase;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.layout.Layout;

import java.util.List;

public class StaticLayout extends PipeBase implements Layout
{
    public static final int NUMBER_OF_RELAYOUTS_NEEDED_FOR_SATISFACTION = 1;

    private final Graph<?> _graph;
    private final ILayout _layout;

    private int _relayoutCount = 0;

    public StaticLayout(Graph graph, ILayout layout)
    {
        _graph = graph;
        _layout = layout;
    }

    public void nodeAdded(String sourceId, long timeId, String nodeId)
    {
        relayoutNeeded();
    }

    public void nodeRemoved(String sourceId, long timeId, String nodeId)
    {
        relayoutNeeded();
    }

    public void edgeAdded(String sourceId, long timeId, String edgeId,
                          String fromId, String toId, boolean directed)
    {
        relayoutNeeded();
    }

    public void edgeRemoved(String sourceId, long timeId, String edgeId)
    {
        relayoutNeeded();
    }

    public void graphCleared(String sourceId, long timeId)
    {
        relayoutNeeded();
    }

    @Override
    public void compute()
    {
        resetLayout();

        computeLayout();

        boolean success = publishLayout();

        if (success)
            _relayoutCount++;
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
        return (double)_relayoutCount / (double) NUMBER_OF_RELAYOUTS_NEEDED_FOR_SATISFACTION;
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

    private void relayoutNeeded()
    {
        _relayoutCount = 0;
    }

    private void resetLayout()
    {
        for (Vertex n : _graph.getVertexes())
        {
            n.reset();
        }
    }

    private boolean publishLayout()
    {
        for (Vertex n : _graph.getVertexes())
        {
            Coordinates c = n.getCoordinates();
            if (c == null)
            {
                return false;
            }

            sendNodeAttributeChanged(sourceId, n.getId(), "xyz", null,
                    new double[] { c.getX(), c.getY(), 0 });
        }

        return true;
    }

    private void computeLayout()
    {
        _layout.apply(_graph);
    }

}
