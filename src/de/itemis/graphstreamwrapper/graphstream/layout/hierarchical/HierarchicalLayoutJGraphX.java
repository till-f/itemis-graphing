package de.itemis.graphstreamwrapper.graphstream.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphstreamwrapper.InternalGraph;
import de.itemis.graphstreamwrapper.InternalNode;
import de.itemis.graphstreamwrapper.graphstream.layout.StaticLayout;

import javax.swing.*;
import java.util.HashMap;

public class HierarchicalLayoutJGraphX extends StaticLayout
{
    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;

    public HierarchicalLayoutJGraphX(InternalGraph internalGraph)
    {
        this(internalGraph, 0.5, 0.5, 0.5);
    }

    public HierarchicalLayoutJGraphX(InternalGraph internalGraph, double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing)
    {
        super(internalGraph);

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
        HashMap<InternalNode, mxCell> nodeToCell = new HashMap<InternalNode, mxCell>();

        graph.getModel().beginUpdate();
        try
        {
            for (InternalNode n : _internalGraph.getNodes())
            {
                mxCell cell = (mxCell) graph.insertVertex(parent, n.getId(), n, 0, 0, n.getWidth(), n.getHeight());
                nodeToCell.put(n, cell);
            }

            for (InternalNode source : _internalGraph.getNodes())
            {
                for (InternalNode target : source.getTargets())
                {
                    mxCell sourceCell = nodeToCell.get(source);
                    mxCell targetCell = nodeToCell.get(target);
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

        for (mxCell vertex : nodeToCell.values())
        {
            mxGeometry geometry = vertex.getGeometry();
            InternalNode n = (InternalNode) vertex.getValue();
            n.place(geometry.getX(), geometry.getY(), true);
        }
    }

}
