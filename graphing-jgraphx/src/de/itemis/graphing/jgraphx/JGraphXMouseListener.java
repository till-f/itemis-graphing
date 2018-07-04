package de.itemis.graphing.jgraphx;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import de.itemis.graphing.model.GraphElement;

import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class JGraphXMouseListener implements MouseInputListener, MouseWheelListener {
    private final JGraphXViewManager<?> _viewManager;
    private final mxGraphComponent _graphComponent;

    private GraphElement<?> _lastTouchedElement = null;

    JGraphXMouseListener(JGraphXViewManager<?> viewManager, mxGraphComponent graphComponent)
    {
        _viewManager = viewManager;
        _graphComponent = graphComponent;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
       GraphElement<?> element = getElement(event);
        _lastTouchedElement = element;
        _viewManager.applyClickInteraction(element == null ? null : element.getId(), true, event);
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        _viewManager.applyClickInteraction(_lastTouchedElement == null ? null : _lastTouchedElement.getId(), false, event);

        if (_lastTouchedElement != null && event.getButton() == 1)
        {
            if (_viewManager.isMultiSelectHotkey(event))
            {
                _viewManager.applySelectInteraction(_lastTouchedElement.getId(), null);
            }
            else
            {
                _viewManager.applySelectInteraction(_lastTouchedElement.getId(), true);
            }
            _viewManager.selectionCompleted();
        }

        _lastTouchedElement = null;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    private GraphElement<?> getElement(MouseEvent e)
    {
        Object cell = _graphComponent.getCellAt(e.getX(), e.getY(), false);

        if (cell instanceof mxCell)
        {
            mxCell mxCell = (mxCell) cell;
            return mxCell.isEdge() ? null : _viewManager.getGraphElement(mxCell);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (e.getWheelRotation() > 0)
        {
            _viewManager.zoomOut();
        }
        else
        {
            _viewManager.zoomIn();
        }
    }
}
