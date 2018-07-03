package de.itemis.graphing.jgraphx;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

public class DefaultMxGraphComponent extends mxGraphComponent
{
    // additional padding around graph - raises probability that whole graph is displayed with fitView()
    // (background: bounding box of graph is calculated wrong by JGraphX! -- Why is EVERY java graph framework just crap with really evil bugs?)
    private final static int AUTOSIZE_GRAPH_PADDING = 10;

    private boolean _isViewLocked = true;

    public DefaultMxGraphComponent(mxGraph mxGraph)
    {
        super(mxGraph);

        this.getViewport().setOpaque(true);
        this.getViewport().setBackground(Color.WHITE);
        this.setCenterZoom(true);
        this.setAutoExtend(true);
        this.setConnectable(false);
        this.setFoldingEnabled(false);
        this.setDragEnabled(false);

        this.addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                if (_isViewLocked)
                {
                    fitView();
                }
            }

            @Override
            public void componentMoved(ComponentEvent e) {  }

            @Override
            public void componentShown(ComponentEvent e) {  }

            @Override
            public void componentHidden(ComponentEvent e) {  }
        });
    }

    @Override
    public boolean isPanningEvent(MouseEvent mouseEvent)
    {
        return SwingUtilities.isRightMouseButton(mouseEvent);
    }

    public void fitView()
    {
        _isViewLocked = true;

        double availWidth = this.getWidth();
        if (availWidth < 10) availWidth = 10;
        double availHeight = this.getHeight();
        if (availHeight < 10) availHeight = 10;

        mxGraphView view = this.getGraph().getView();
        view.setScale(1.0);

        mxRectangle bounds = view.getGraphBounds();
        double width = bounds.getWidth() + AUTOSIZE_GRAPH_PADDING * 2;
        double height = bounds.getHeight() + AUTOSIZE_GRAPH_PADDING * 2;

        double scaleW = availWidth/width;
        double scaleH = availHeight/height;
        double newScale = Math.min(scaleW, scaleH);
        view.setScale(newScale);

        double newWidth = bounds.getWidth() * newScale;
        double newHeight = bounds.getHeight() * newScale;
        double xOffs = (availWidth - newWidth) / 2 / newScale;
        double yOffs = (availHeight - newHeight) / 2 / newScale;

        this.getGraph().getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(xOffs, yOffs, 0, 0));
    }

    @Override
    public void zoom(double factor)
    {
        _isViewLocked = false;
        super.zoom(factor);
    }

    @Override
    public void zoomTo(double newScale, boolean center)
    {
        _isViewLocked = false;
        super.zoomTo(newScale, center);
    }

    @Override
    public void zoomActual()
    {
        _isViewLocked = false;
        super.zoomActual();
    }

}
