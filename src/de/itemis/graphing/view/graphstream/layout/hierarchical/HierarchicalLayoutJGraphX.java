package de.itemis.graphing.view.graphstream.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;
import de.itemis.graphing.view.graphstream.layout.StaticLayout;

import javax.swing.*;
import java.util.HashMap;

public class HierarchicalLayoutJGraphX extends StaticLayout
{
    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;

    public HierarchicalLayoutJGraphX(Graph graph)
    {
        this(graph, 0.5, 0.5, 0.5);
    }

    public HierarchicalLayoutJGraphX(Graph graph, double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing)
    {
        super(graph);

        _intraCellSpacing = intraCellSpacing;
        _interHierarchySpacing = interHierarchySpacing;
        _interRankCellSpacing = interRankCellSpacing;
    }

    @Override
    public String getLayoutAlgorithmName()
    {
        return "Hierarchical Layout (JGraphX)";
    }

    @Override
    protected void computeLayout()
    {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        HashMap<Vertex, mxCell> vertexToCell = new HashMap<Vertex, mxCell>();

        graph.getModel().beginUpdate();
        try
        {
            for (Vertex n : _graph.getVertexes())
            {
                mxCell cell = (mxCell) graph.insertVertex(parent, n.getId(), n, 0, 0, n.getWidth(), n.getHeight());
                vertexToCell.put(n, cell);
            }

            for (Vertex source : _graph.getVertexes())
            {
                for (Vertex target : source.getTargets())
                {
                    mxCell sourceCell = vertexToCell.get(source);
                    mxCell targetCell = vertexToCell.get(target);
                    graph.insertEdge(parent, null, "edge", sourceCell, targetCell);
                }
            }

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, SwingConstants.SOUTH);
            layout.setFineTuning(true);
            layout.setIntraCellSpacing(_intraCellSpacing);
            layout.setInterHierarchySpacing(_interHierarchySpacing);
            layout.setInterRankCellSpacing(_interRankCellSpacing);
            layout.execute(parent);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        for (mxCell vertex : vertexToCell.values())
        {
            mxGeometry geometry = vertex.getGeometry();
            Vertex n = (Vertex) vertex.getValue();
            n.place(geometry.getX(), geometry.getY(), true);
        }
    }

}
