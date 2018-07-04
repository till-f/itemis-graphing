package de.itemis.graphing.jgraphx.customization;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.view.mxGraph;

public class DefaultMxHierarchicalLayout extends mxHierarchicalLayout
{

    public DefaultMxHierarchicalLayout(mxGraph mxGraph, double spacingIntraCell, double spacingInterHierarchy, double spacingInterRank)
    {
        super(mxGraph);
        this.setFineTuning(true);
        this.setIntraCellSpacing(spacingIntraCell);
        this.setInterHierarchySpacing(spacingInterHierarchy);
        this.setInterRankCellSpacing(spacingInterRank);
        this.setResizeParent(false);
        this.setMoveParent(false);
        this.setUseBoundingBox(false);
    }

}
