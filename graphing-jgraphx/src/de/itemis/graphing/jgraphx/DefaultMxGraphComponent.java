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

    private boolean _isViewLocked = true;
    private int _graphPadding = 5;

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

        mxGraphView view = this.getGraph().getView();

        double availWidth = this.getWidth() - _graphPadding * 2;
        if (availWidth < 10) availWidth = 10;
        double availHeight = this.getHeight() - _graphPadding * 2;
        if (availHeight < 10) availHeight = 10;

        mxRectangle bounds = view.getGraphBounds();
        double width = bounds.getWidth();
        double height = bounds.getHeight();

        double scaleW = availWidth/width * view.getScale();
        double scaleH = availHeight/height * view.getScale();
        double newScale = Math.min(scaleW, scaleH);
        view.setScale(newScale);

        mxRectangle newBounds = view.getGraphBounds();
        double newWidth = newBounds.getWidth();
        double newHeight = newBounds.getHeight();

        double wOffs = Math.abs(availWidth - newWidth) / 2 / newScale + _graphPadding;
        double hOffs = Math.abs(availHeight - newHeight) / 2 / newScale + _graphPadding;

        this.getGraph().getModel().setGeometry(graph.getDefaultParent(), new mxGeometry(wOffs, hOffs, newWidth, newHeight));
    }

}
