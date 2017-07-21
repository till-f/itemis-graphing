package de.itemis.graphstreamwrapper.layout.hierarchical;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
import de.itemis.graphstreamwrapper.layout.InternalNode;
import de.itemis.graphstreamwrapper.layout.StaticLayout;
import org.jgraph.JGraph;
import org.jgraph.graph.*;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class HierarchicalLayoutJGraph extends StaticLayout
{

    @Override
    public String getLayoutAlgorithmName() {
        return "Hierarchical Layout";
    }

    @Override
    public void computeLayout()
    {
        GraphModel model = new DefaultGraphModel();
        JGraph jgraph = new JGraph(model);

        HashMap<InternalNode, DefaultGraphCell> nodeToCell = new HashMap<InternalNode, DefaultGraphCell>();

        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            DefaultGraphCell cell = new DefaultGraphCell(n);
            GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(0,0,3,3));
            nodeToCell.put(n, cell);
        }

        jgraph.getGraphLayoutCache().insert(nodeToCell.values().toArray());

        for (InternalNode source : _nodeIDToNodeMap.values())
        {
            for (InternalNode target : source.getTargets())
            {
                DefaultEdge edge = new DefaultEdge();
                DefaultGraphCell sourceCell = nodeToCell.get(source);
                DefaultGraphCell targetCell = nodeToCell.get(target);
                edge.setSource(sourceCell);
                edge.setTarget(targetCell);
                jgraph.getGraphLayoutCache().insertEdge(edge, sourceCell, targetCell);
            }
        }

        JGraphHierarchicalLayout hierarchicalLayout = new JGraphHierarchicalLayout();
        JGraphFacade jGraphFacade = new JGraphFacade(jgraph);
        hierarchicalLayout.run(jGraphFacade);
        Map changes = jGraphFacade.createNestedMap(false, false);
        jgraph.getGraphLayoutCache().edit(changes);

        for (DefaultGraphCell cell : nodeToCell.values())
        {
            Rectangle2D pos = jgraph.getCellBounds(cell);
            InternalNode n = (InternalNode) cell.getUserObject();
            n.place(pos.getX(), pos.getY());
            System.out.println(n.getID() + ": " + pos.getCenterX() + ", " + pos.getCenterY());
        }
    }
}
