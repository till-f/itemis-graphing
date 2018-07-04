package de.itemis.graphing.jgraphx.customization;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.view.mxGraph;

public class DefaultMxHierarchicalLayout extends mxHierarchicalLayout
{

    public DefaultMxHierarchicalLayout(mxGraph mxGraph)
    {
        super(mxGraph);
        this.setFineTuning(true);
        this.setIntraCellSpacing(15);
        this.setInterHierarchySpacing(15);
        this.setInterRankCellSpacing(30);
        this.setResizeParent(false);
        this.setMoveParent(false);
        this.setUseBoundingBox(false);
    }

}
