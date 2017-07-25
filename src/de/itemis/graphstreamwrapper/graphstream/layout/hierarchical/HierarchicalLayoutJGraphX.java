package de.itemis.graphstreamwrapper.graphstream.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphstreamwrapper.InternalNode;
import de.itemis.graphstreamwrapper.graphstream.layout.StaticLayout;
import org.graphstream.graph.Graph;

import javax.swing.*;
import java.util.HashMap;

public class HierarchicalLayoutJGraphX extends StaticLayout
{
    private final double _intraCellSpacing;
    private final double _interHierarchySpacing;
    private final double _interRankCellSpacing;

    public HierarchicalLayoutJGraphX(Graph originalGraph)
    {
        this(originalGraph, 0.5, 0.5, 0.5);
    }

    public HierarchicalLayoutJGraphX(Graph originalGraph, double intraCellSpacing, double interHierarchySpacing, double interRankCellSpacing)
    {
        super(originalGraph);

        _intraCellSpacing = intraCellSpacing;
        _interHierarchySpacing = interHierarchySpacing;
        _interRankCellSpacing = interRankCellSpacing;
    }

    @Override
    public String getLayoutAlgorithmName()
    {
        return "Hierarchical Layout X";
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
            for (InternalNode n : _nodeIDToNodeMap.values())
            {
                mxCell cell = (mxCell) graph.insertVertex(parent, n.getID(), n, 0, 0, n.getWidth(), n.getHeight());
                nodeToCell.put(n, cell);
            }

            for (InternalNode source : _nodeIDToNodeMap.values())
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
            n.place(geometry.getX(), geometry.getY());
        }
    }

}
