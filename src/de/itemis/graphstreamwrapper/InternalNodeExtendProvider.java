package de.itemis.graphstreamwrapper;

import org.abego.treelayout.NodeExtentProvider;

public class InternalNodeExtendProvider implements  NodeExtentProvider<InternalNode>
{
    @Override
    public double getWidth(InternalNode treeNode)
    {
        return treeNode.getWidth();
    }

    @Override
    public double getHeight(InternalNode treeNode)
    {
        return treeNode.getHeight();
    }
}

