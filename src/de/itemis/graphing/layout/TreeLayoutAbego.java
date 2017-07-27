package de.itemis.graphing.layout;

import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.Vertex;
import org.abego.treelayout.NodeExtentProvider;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

public class TreeLayoutAbego implements ILayout
{
    public class VertexExtendProvider implements NodeExtentProvider<Vertex>
    {
        @Override
        public double getWidth(Vertex treeNode)
        {
            return treeNode.getFinalWidth();
        }

        @Override
        public double getHeight(Vertex treeNode)
        {
            return treeNode.getFinalHeight();
        }
    }

    private final DefaultConfiguration<Vertex> _layouterConfiguration;
    private final VertexExtendProvider _nodeExtentProvider;

    private double _gapBetweenLevels;
    private double _gapBetweenNodes;

    public TreeLayoutAbego()
    {
        this(0.5, 0.5);
    }

    public TreeLayoutAbego(double gapBetweenLevels, double gapBetweenNodes)
    {
        _gapBetweenLevels = gapBetweenLevels;
        _gapBetweenNodes = gapBetweenNodes;
        _layouterConfiguration = new DefaultConfiguration<Vertex>(gapBetweenLevels, gapBetweenNodes);
        _nodeExtentProvider = new VertexExtendProvider();
    }

    @Override
    public void apply(Graph g)
    {
        double xOffset = 0;
        for (Vertex root : g.getRootVertexes())
        {
            DefaultTreeForTreeLayout<Vertex> tree = new DefaultTreeForTreeLayout<Vertex>(root);
            fillAbegoTree_recursive(tree, root);

            org.abego.treelayout.TreeLayout<Vertex> treeLayout = new org.abego.treelayout.TreeLayout<Vertex>(tree, _nodeExtentProvider, _layouterConfiguration);
            for(Vertex n : treeLayout.getNodeBounds().keySet())
            {
                n.place(treeLayout.getNodeBounds().get(n).x + xOffset, treeLayout.getNodeBounds().get(n).y * -1, false);
            }
            xOffset += treeLayout.getBounds().getWidth() + _gapBetweenNodes;
        }
    }

    private void fillAbegoTree_recursive(DefaultTreeForTreeLayout<Vertex> tree, Vertex parent)
    {
        for(Vertex child : parent.getTargets())
        {
            if (tree.hasNode(child))
                continue;

            tree.addChildren(parent, child);
            fillAbegoTree_recursive(tree, child);
        }
    }
}

