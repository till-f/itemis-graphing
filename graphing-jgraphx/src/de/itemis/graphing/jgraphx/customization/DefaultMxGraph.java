package de.itemis.graphing.jgraphx.customization;

import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

public class DefaultMxGraph extends mxGraph
{

    public DefaultMxGraph()
    {
        this.setCellsDisconnectable(false);
        this.setAutoOrigin(true);
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

    @Override
    protected mxGraphView createGraphView()
    {
        return new mxGraphView(this)
        {
            @Override
            public void updateLabelBounds(mxCellState state)
            {
                if (model.isVertex(state.getCell()))
                {
                    // hack because vertical alignment of labels seems to be buggy... shitty crap everywhere...
                    mxRectangle labelRect = mxUtils.getLabelSize(state.getLabel(), state.getStyle(), isHtmlLabel(state.getCell()), getView().getScale());
                    mxRectangle resultRect = new mxRectangle(state);
                    double newY = resultRect.getY() + (resultRect.getHeight() - labelRect.getHeight()) / 2;
                    resultRect.setY(newY);
                    resultRect.setHeight(labelRect.getHeight());
                    state.setLabelBounds(resultRect);
                }
                else
                {
                    super.updateLabelBounds(state);
                }
            }
        };
    }
}
