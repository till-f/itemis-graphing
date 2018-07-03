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

    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;
    private final int _hierarchyDirection;

    public HierarchicalLayoutJGraphX()
    {
        this(EHierarchyDirection.SOUTH);
    }

    public HierarchicalLayoutJGraphX(EHierarchyDirection hierarchyDirection)
    {
        this(15, 15, 30, hierarchyDirection);
    }

    public HierarchicalLayoutJGraphX(double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing, EHierarchyDirection hierarchyDirection)
    {
        _intraCellSpacing = intraCellSpacing;
        _interHierarchySpacing = interHierarchySpacing ;
        _interRankCellSpacing = interRankCellSpacing;

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
                mxCell cell = (mxCell) mxGraph.insertVertex(parent, n.getId(), n, 0, 0, n.getSize().getWidth(), n.getSize().getHeight());
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
            layout.setResizeParent(false);
            layout.setMoveParent(false);
            layout.setUseBoundingBox(false);
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
            n.place(geometry.getX(), geometry.getY(), true);
        }
    }
}
