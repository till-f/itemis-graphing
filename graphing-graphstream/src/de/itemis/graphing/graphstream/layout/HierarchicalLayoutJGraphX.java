package de.itemis.graphing.graphstream.layout;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class HierarchicalLayoutJGraphX implements ILayout
{
    public enum EHierarchyDirection { WEST, SOUTH, EAST, NORTH;}

    private static final int SCALE_FACTOR = 100;

    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;
    private final int _hierarchyDirection;

    public HierarchicalLayoutJGraphX()
    {
        this(0.6, 0.6, 0.6, EHierarchyDirection.SOUTH);
    }

    public HierarchicalLayoutJGraphX(EHierarchyDirection hierarchyDirection)
    {
        this(0.6, 0.6, 0.6, hierarchyDirection);
    }

    public HierarchicalLayoutJGraphX(double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing, EHierarchyDirection hierarchyDirection)
    {
        _intraCellSpacing = intraCellSpacing * SCALE_FACTOR;
        _interHierarchySpacing = interHierarchySpacing * SCALE_FACTOR;
        _interRankCellSpacing = interRankCellSpacing * SCALE_FACTOR;

        // get rid of the insane practice in java to use int for everything
        switch (hierarchyDirection)
        {
            case WEST:
                _hierarchyDirection = SwingConstants.WEST;
                break;
            case SOUTH:
                _hierarchyDirection = SwingConstants.SOUTH;
                break;
            case EAST:
                _hierarchyDirection = SwingConstants.EAST;
                break;
            case NORTH:
                _hierarchyDirection = SwingConstants.NORTH;
                break;
            default:
                throw new RuntimeException("invalid hierarchyDirection: " + hierarchyDirection);
        }
    }

    @Override
    public void apply(Graph<?> graph)
    {
        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();
        HashMap<Vertex, mxCell> vertexToCell = new HashMap<Vertex, mxCell>();

        mxGraph.getModel().beginUpdate();
        try
        {
            for (Vertex n : graph.getVertexes())
            {
                mxCell cell = (mxCell) mxGraph.insertVertex(parent, n.getId(), n, 0, 0, n.getSize().getWidth()*SCALE_FACTOR, n.getSize().getHeight()*SCALE_FACTOR);
                vertexToCell.put(n, cell);
            }

            for (Vertex<?> source : graph.getVertexes())
            {
                for (Vertex target : source.getTargets())
                {
                    mxCell sourceCell = vertexToCell.get(source);
                    mxCell targetCell = vertexToCell.get(target);
                    mxGraph.insertEdge(parent, null, "edge", sourceCell, targetCell);
                }
            }

            mxHierarchicalLayout layout = new mxHierarchicalLayout(mxGraph, _hierarchyDirection);
            layout.setFineTuning(true);
            layout.setIntraCellSpacing(_intraCellSpacing);
            layout.setInterHierarchySpacing(_interHierarchySpacing);
            layout.setInterRankCellSpacing(_interRankCellSpacing);
            layout.execute(parent);
        }
        finally
        {
            mxGraph.getModel().endUpdate();
        }

        for (mxCell vertex : vertexToCell.values())
        {
            mxGeometry geometry = vertex.getGeometry();
            Vertex n = (Vertex) vertex.getValue();
            n.place(geometry.getX() / SCALE_FACTOR, geometry.getY() / SCALE_FACTOR, true);
        }
    }
}
