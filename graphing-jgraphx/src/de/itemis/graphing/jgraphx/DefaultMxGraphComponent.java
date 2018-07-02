package de.itemis.graphing.jgraphx;

import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
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
        if (SwingUtilities.isRightMouseButton(mouseEvent))
        {
            return true;
        }
        return false;
    }

    public void fitView()
    {
        _isViewLocked = true;

        mxGraphView view = this.getGraph().getView();

        double availWidth = this.getWidth() - _graphPadding * 2;
        if (availWidth < 10) availWidth = 10;
        double availHeight = this.getHeight() - _graphPadding * 2;
        if (availHeight < 10) availHeight = 10;

        double width = view.getGraphBounds().getWidth();
        double height = view.getGraphBounds().getHeight();

        double scaleW = availWidth/width * view.getScale();
        double scaleH = availHeight/height * view.getScale();
        double newScale = Math.min(scaleW, scaleH);
        view.setScale(newScale);

        double newWidth = view.getGraphBounds().getWidth();
        double newHeight = view.getGraphBounds().getHeight();

        double wOffs = Math.abs(availWidth - newWidth) / 2 / newScale + _graphPadding;
        double hOffs = Math.abs(availHeight - newHeight) / 2 / newScale + _graphPadding;

        this.getGraph().getModel().setGeometry(this.getGraph().getDefaultParent(), new mxGeometry(wOffs, hOffs, newWidth, newHeight));
    }

}
