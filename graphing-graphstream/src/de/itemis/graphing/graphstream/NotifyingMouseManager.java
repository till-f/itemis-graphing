package de.itemis.graphing.graphstream;

import de.itemis.graphing.view.IViewManager;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.MouseManager;

import java.awt.event.MouseEvent;

public class NotifyingMouseManager implements MouseManager
{
    public class MousePan
    {
        private Camera _camera;
        private boolean _isPanning;
        private double _x;
        private double _y;

        public MousePan(Camera camera)
        {
            _camera = camera;
        }

        public void startPan(double x, double y)
        {
            _isPanning = true;
            _x = x;
            _y = y;
        }

        public void endPan()
        {
            _isPanning = false;
        }

        public void update(double x, double y)
        {
            if (_isPanning)
            {
                double xOffs = _x - x;
                double yOffs = _y - y;
                _x = x;
                _y = y;

                Point3 oldCenter = _camera.getViewCenter();
                Point3 oldCenterPX = _camera.transformGuToPx(oldCenter.x, oldCenter.y, oldCenter.z);
                Point3 newCenter = _camera.transformPxToGu(oldCenterPX.x + xOffs, oldCenterPX.y + yOffs);

                _camera.setViewCenter(newCenter.x, newCenter.y, oldCenter.z);
            }
        }
    }

    protected final IViewManager _viewManager;

    protected ViewPanel _view;
    protected GraphicGraph _gsGraph;
    protected Camera _camera;

    protected GraphicElement _lastTouchedElement;
    protected GraphicElement _lastStreakedElement;
    protected float _selectionX;
    protected float _selectionY;
    protected MousePan _mousePan;

    public NotifyingMouseManager(IViewManager viewManager)
    {
        _viewManager = viewManager;
    }

    @Override
    public void init(GraphicGraph gsGraph, View view)
    {
        _gsGraph = gsGraph;
        _view = (ViewPanel) view;
        _mousePan = new MousePan(view.getCamera());
        _camera = view.getCamera();

        view.addMouseListener(this);
        view.addMouseMotionListener(this);
    }

    @Override
    public void release()
    {
        _view.removeMouseListener(this);
        _view.removeMouseMotionListener(this);
    }

    protected void mouseButtonPress(MouseEvent event)
    {
        _view.requestFocus();

        if (event.getButton() == 1)
        {
            // unselect all.
            if (!event.isShiftDown())
            {
                for (Node node : _gsGraph)
                {
                    _viewManager.applySelectInteraction(node.getId(), false);
                }

                for (GraphicSprite sprite : _gsGraph.spriteSet())
                {
                    _viewManager.applySelectInteraction(sprite.getId(), false);
                }
                _viewManager.selectionCompleted();
            }
        }
    }

    protected void selectElementsInArea(Iterable<GraphicElement> elementsInArea)
    {
        for (GraphicElement gsElement : elementsInArea)
        {
            _viewManager.applySelectInteraction(gsElement.getId(), true);
        }
        _viewManager.selectionCompleted();
    }

    protected void elementMoving(GraphicElement element, MouseEvent event)
    {
        _view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonPressOnElement(GraphicElement gsElement, MouseEvent event)
    {
        _view.freezeElement(gsElement, true);
        _viewManager.applyClickInteraction(gsElement.getId(), true, event);
    }

    protected void mouseButtonReleaseFromElement(GraphicElement gsElement, MouseEvent event)
    {
        _view.freezeElement(gsElement, false);
        _viewManager.applyClickInteraction(gsElement.getId(), false, event);

        if (event.getButton() == 1)
        {

            if (event.isShiftDown())
            {
                _viewManager.applySelectInteraction(gsElement.getId(), null);
            }
            else
            {
                _viewManager.applySelectInteraction(gsElement.getId(), true);
            }
            _viewManager.selectionCompleted();
        }
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // NOP
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        _view.selectionGrowsAt(event.getX(), event.getY());
        _mousePan.update(event.getX(), event.getY());
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // NOP
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // NOP
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        GraphicElement streakedElement = _view.findNodeOrSpriteAt(event.getX(), event.getY());

        if ((streakedElement != null || _lastStreakedElement != null) && streakedElement != _lastStreakedElement)
        {
            String enterId = streakedElement == null ? null : streakedElement.getId();
            String exitId = _lastStreakedElement == null ? null : _lastStreakedElement.getId();
            _viewManager.applyHoverInteraction(enterId, exitId, event);
            _lastStreakedElement = streakedElement;
        }
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        mouseButtonPress(event);

        _lastTouchedElement = _view.findNodeOrSpriteAt(event.getX(), event.getY());

        if (_lastTouchedElement != null)
        {
            mouseButtonPressOnElement(_lastTouchedElement, event);
        }
        else
        {
            _viewManager.applyClickInteraction(null, true, event);

            if (event.getButton() == 1)
            {
                _selectionX = event.getX();
                _selectionY = event.getY();
                _view.beginSelectionAt(_selectionX, _selectionY);
            }
        }

        if (event.getButton() == 3)
        {
            _mousePan.startPan(event.getX(), event.getY());
        }

    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        if (_lastTouchedElement != null)
        {
            mouseButtonReleaseFromElement(_lastTouchedElement, event);
            _lastTouchedElement = null;
        }
        else
        {
            _viewManager.applyClickInteraction(null, false, event);

            if (event.getButton() == 1)
            {
                float x2 = event.getX();
                float y2 = event.getY();
                float t;

                if (_selectionX > x2)
                {
                    t = _selectionX;
                    _selectionX = x2;
                    x2 = t;
                }
                if (_selectionY > y2)
                {
                    t = _selectionY;
                    _selectionY = y2;
                    y2 = t;
                }
                _view.endSelectionAt(x2, y2);
                selectElementsInArea(_view.allNodesOrSpritesIn(_selectionX, _selectionY, x2, y2));
            }
        }

        if (event.getButton() == 3)
        {
            _mousePan.endPan();
        }
    }
}
