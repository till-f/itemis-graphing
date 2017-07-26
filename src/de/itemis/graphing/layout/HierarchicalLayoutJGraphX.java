package de.itemis.graphing.layout;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;

import javax.swing.*;
import java.util.HashMap;

public class HierarchicalLayoutJGraphX implements Layout
{
    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;

    public HierarchicalLayoutJGraphX()
    {
        this(0.5, 0.5, 0.5);
    }

    public HierarchicalLayoutJGraphX(double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing)
    {
        _intraCellSpacing = intraCellSpacing;
        _interHierarchySpacing = interHierarchySpacing;
        _interRankCellSpacing = interRankCellSpacing;
    }

    @Override
    public void apply(Graph graph)
    {
        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();
        HashMap<Vertex, mxCell> vertexToCell = new HashMap<Vertex, mxCell>();

        mxGraph.getModel().beginUpdate();
        try
        {
            for (Vertex n : graph.getVertexes())
            {
                mxCell cell = (mxCell) mxGraph.insertVertex(parent, n.getId(), n, 0, 0, n.getWidth(), n.getHeight());
                vertexToCell.put(n, cell);
            }

            for (Vertex source : graph.getVertexes())
            {
                for (Vertex target : source.getTargets())
                {
                    mxCell sourceCell = vertexToCell.get(source);
                    mxCell targetCell = vertexToCell.get(target);
                    mxGraph.insertEdge(parent, null, "edge", sourceCell, targetCell);
                }
            }

            mxHierarchicalLayout layout = new mxHierarchicalLayout(mxGraph, SwingConstants.SOUTH);
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
            n.place(geometry.getX(), geometry.getY(), true);
        }
    }
}
