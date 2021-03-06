package de.itemis.graphing.jgraphx.customization;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import de.itemis.graphing.jgraphx.JGraphXViewManager;

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

    private final static double MAX_ZOOM_SCALE = 10;    // don't allow endless zoom in
    private final static double MIN_ZOOM_SCALE = 0.04;  // 0.04 is a hard-coded value inside mxGraphComponent and zoom stops working if scale is lower

    private final JGraphXViewManager<?> _viewManager;

    private boolean _isViewLocked = true;

    public DefaultMxGraphComponent(mxGraph mxGraph, JGraphXViewManager<?> viewManager)
    {
        super(mxGraph);
        _viewManager = viewManager;

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

        mxGraphView view = graph.getView();
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

        graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(xOffs, yOffs, 0, 0));
    }

    @Override
    public void zoomIn()
    {
        mxGraphView view = graph.getView();
        double scale = view.getScale();

        if (scale <= MIN_ZOOM_SCALE)
        {
            zoomTo(MIN_ZOOM_SCALE + 0.01, true);
        }
        else if (scale <= MAX_ZOOM_SCALE)
        {
            super.zoomIn();
        }
    }

    @Override
    public void zoom(double factor)
    {
        unlockView();
        super.zoom(factor);
    }

    @Override
    public void zoomTo(double newScale, boolean center)
    {
        unlockView();
        super.zoomTo(newScale, center);
    }

    @Override
    public void zoomActual()
    {
        unlockView();
        super.zoomActual();
    }

    private void unlockView()
    {
        _isViewLocked = false;
        graph.getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(AUTOSIZE_GRAPH_PADDING, AUTOSIZE_GRAPH_PADDING, 0, 0));
    }

}
