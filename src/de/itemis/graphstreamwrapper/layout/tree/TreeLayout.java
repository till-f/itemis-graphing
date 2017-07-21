package de.itemis.graphstreamwrapper.layout.tree;

import de.itemis.graphstreamwrapper.layout.InternalNode;
import de.itemis.graphstreamwrapper.layout.StaticLayout;
import org.abego.treelayout.util.DefaultConfiguration;
import org.abego.treelayout.util.DefaultTreeForTreeLayout;

public class TreeLayout extends StaticLayout
{
    protected final DefaultConfiguration<InternalNode> _layouterConfiguration;
    protected final InternalNodeExtendProvider _nodeExtentProvider;

    protected double _gapBetweenLevels;
    protected double _gapBetweenNodes;

    public TreeLayout()
    {
        this(0.5, 0.5);
    }

    public TreeLayout(double gapBetweenLevels, double gapBetweenNodes)
    {
        _gapBetweenLevels = gapBetweenLevels;
        _gapBetweenNodes = gapBetweenNodes;
        _layouterConfiguration = new DefaultConfiguration<InternalNode>(gapBetweenLevels, gapBetweenNodes);
        _nodeExtentProvider = new InternalNodeExtendProvider();
    }

    @Override
    public void compute()
    {
        resetLayout();

        computeLayout();

        publishLayout();

        _isLayouted = true;
        _lastComputeTime = System.currentTimeMillis();
    }

    private void resetLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            n.reset();
        }
    }

    private void computeLayout()
    {
        double xOffset = 0;
        for (InternalNode root : getRootNodes())
        {
            DefaultTreeForTreeLayout<InternalNode> tree = new DefaultTreeForTreeLayout<InternalNode>(root);
            fillAbegoTree_recursive(tree, root);

            org.abego.treelayout.TreeLayout<InternalNode> treeLayout = new org.abego.treelayout.TreeLayout<InternalNode>(tree, _nodeExtentProvider, _layouterConfiguration);
            for(InternalNode n : treeLayout.getNodeBounds().keySet())
            {
                n.place(treeLayout.getNodeBounds().get(n).x + xOffset, treeLayout.getNodeBounds().get(n).y * -1);
            }
            xOffset += treeLayout.getBounds().getWidth() + _gapBetweenNodes;
        }
    }

    private void fillAbegoTree_recursive(DefaultTreeForTreeLayout<InternalNode> tree, InternalNode parent)
    {
        for(InternalNode child : parent.getTargets())
        {
            if (tree.hasNode(child))
                continue;

            tree.addChildren(parent, child);
            fillAbegoTree_recursive(tree, child);
        }
    }

    private void publishLayout()
    {
        for (InternalNode n : _nodeIDToNodeMap.values())
        {
            sendNodeAttributeChanged(sourceId, n.getID(), "xyz", null,
                    new double[] { n.getX(), n.getY(), 0 });
        }
    }

    @Override
    public String getLayoutAlgorithmName()
    {
        return "Tree Layout";
    }

}
