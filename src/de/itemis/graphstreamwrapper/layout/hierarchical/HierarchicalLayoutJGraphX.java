package de.itemis.graphstreamwrapper.layout.hierarchical;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import de.itemis.graphstreamwrapper.layout.InternalNode;
import de.itemis.graphstreamwrapper.layout.StaticLayout;

import javax.swing.*;
import java.util.HashMap;

public class HierarchicalLayoutJGraphX extends StaticLayout
{
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
                mxCell cell = (mxCell) graph.insertVertex(parent, n.getID(), n, 0, 0, 0.5, 0.5);
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
            layout.setIntraCellSpacing(0.5);
            layout.setInterHierarchySpacing(0.5);
            layout.setInterRankCellSpacing(0.5);
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
