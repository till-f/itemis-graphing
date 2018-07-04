package de.itemis.graphing.jgraphx.customization;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class DefaultMxGraph extends mxGraph
{

    public DefaultMxGraph()
    {
        this.setCellsDisconnectable(false);
    }

    @Override
    public boolean isCellMovable(Object o)
    {
        return false;
    }

    @Override
    public boolean isCellConnectable(Object o)
    {
        return false;
    }

    @Override
    public boolean isCellResizable(Object cell)
    {
        return false;
    }

    @Override
    public boolean isCellEditable(Object cell)
    {
        return false;
    }

    @Override
    public boolean isCellSelectable(Object cell)
    {
        return false;
    }

}
