package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.view.IInteractionListener;
import org.graphstream.graph.Node;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.graphicGraph.GraphicSprite;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Camera;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.util.MouseManager;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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

    protected final GraphstreamViewManager _viewManager;
    protected final boolean _allowDragNodes = false;        // not used atm
    protected final boolean _allowDragSprites = false;      // not used atm

    protected ViewPanel _view;
    protected GraphicGraph _gsGraph;
    protected Camera _camera;

    protected GraphicElement _touchedElement;
    protected GraphicElement _dragElement;
    protected float _selectionX;
    protected float _selectionY;
    protected MousePan _mousePan;

    public NotifyingMouseManager(GraphstreamViewManager viewManager)
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
                    _viewManager.applyInteraction(node.getId(), false, null, null, null, event);
                }

                for (GraphicSprite sprite : _gsGraph.spriteSet())
                {
                    _viewManager.applyInteraction(sprite.getId(), false, null, null, null, event);
                }
                _viewManager.notifySelectionChanged();
            }
        }

        _dragElement = null;
    }

    protected void mouseButtonRelease(MouseEvent event)
    {
        _dragElement = null;
    }

    protected void selectElementsInArea(Iterable<GraphicElement> elementsInArea)
    {
        for (GraphicElement element : elementsInArea)
        {
            _viewManager.applyInteraction(element.getId(), true, null, null, null, null);
        }
        _viewManager.notifySelectionChanged();
    }

    protected void elementMoving(GraphicElement element, MouseEvent event)
    {
        _view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonPressOnElement(GraphicElement gsElement, MouseEvent event)
    {
        _view.freezeElement(gsElement, true);

        if (event.getButton() == 1)
        {
            _viewManager.applyInteraction(gsElement.getId(), null, null, true, null, event);
        }
    }

    protected void mouseButtonReleaseFromElement(GraphicElement gsElement, MouseEvent event)
    {
        _view.freezeElement(gsElement, false);

        if (event.getButton() == 1)
        {
            _viewManager.applyInteraction(gsElement.getId(), null, null, null, true, event);

            if (event.isShiftDown())
            {
                _viewManager.applyInteraction(gsElement.getId(), null, true, null, null, event);
            }
            else
            {
                _viewManager.applyInteraction(gsElement.getId(), true, null, null, null, event);
            }
            _viewManager.notifySelectionChanged();
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
        if (_dragElement != null)
        {
            elementMoving(_dragElement, event);
        }
        else
        {
            _view.selectionGrowsAt(event.getX(), event.getY());
            _mousePan.update(event.getX(), event.getY());
        }
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
    public void mouseMoved(MouseEvent e)
    {
        if (!_view.hasFocus())
        {
            // this is a hotfix for MPS: they seem to do something totally strange things to update/redraw the UI.
            // the camera does not seem to be in synch after the view lost the focus in MPS.
            // (pixels are not transformed into graphical units correctly or something...)
            // it did not work out to add a focus listener, so this is the hacky solution. love you, MPS.
            _view.getCamera().setViewPercent(_view.getCamera().getViewPercent());
        }
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        mouseButtonPress(event);

        _touchedElement = _view.findNodeOrSpriteAt(event.getX(), event.getY());

        if (_touchedElement != null)
        {
            if (((_touchedElement instanceof GraphicSprite) && _allowDragSprites) ||
                ((_touchedElement instanceof GraphicNode) && _allowDragNodes))
            {
                _dragElement = _touchedElement;
            }
            mouseButtonPressOnElement(_touchedElement, event);
        }
        else if (event.getButton() == 1)
        {
            _viewManager.notifyClickBegin(null, new IInteractionListener.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown()));

            _selectionX = event.getX();
            _selectionY = event.getY();
            _view.beginSelectionAt(_selectionX, _selectionY);
        }

        if (event.getButton() == 3)
        {
            _mousePan.startPan(event.getX(), event.getY());
        }

    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        mouseButtonRelease(event);

        if (_touchedElement != null)
        {
            mouseButtonReleaseFromElement(_touchedElement, event);
            _touchedElement = null;
        }
        else if (event.getButton() == 1)
        {
            _viewManager.notifyClickEnd(null, new IInteractionListener.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown()));

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

        if (event.getButton() == 3)
        {
            _mousePan.endPan();
        }
    }
}
