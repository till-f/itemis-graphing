package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.listeners.IInteractionListener;
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;

public class NotifyingMouseManager implements MouseManager, MouseWheelListener
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
    protected final boolean _allowDragNodes;
    protected final boolean _allowDragSprites;
    protected final HashSet<IInteractionListener> _interactionListeners = new HashSet<IInteractionListener>();

    protected ViewPanel _view;
    protected GraphicGraph _graph;
    protected Camera _camera;

    protected GraphicElement _touchedElement;
    protected GraphicElement _dragElement;
    protected float _selectionX;
    protected float _selectionY;
    protected MousePan _mousePan;

    public NotifyingMouseManager(GraphstreamViewManager viewManager)
    {
        this(viewManager, false, false);
    }

    public NotifyingMouseManager(GraphstreamViewManager viewManager, boolean allowDragNodes, boolean allowDragSprites)
    {
        _viewManager = viewManager;
        _allowDragNodes = allowDragNodes;
        _allowDragSprites = allowDragSprites;
    }

    @Override
    public void init(GraphicGraph graph, View view)
    {
        if (view instanceof ViewPanel)
            _view = (ViewPanel)view;
        else
            throw new IllegalArgumentException("NotifyingMouseManager requires ViewPanel instance for 'view'");

        _graph = graph;
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

    public void registerInteractionListener(IInteractionListener listener)
    {
        _interactionListeners.add(listener);
    }

    protected void mouseButtonPress(MouseEvent event)
    {
        _view.requestFocus();

        if (event.getButton() == 1)
        {
            // unselect all.
            if (!event.isShiftDown())
            {
                for (Node node : _graph)
                {
                    if (node.hasAttribute("ui.selected"))
                        node.removeAttribute("ui.selected");
                }

                for (GraphicSprite sprite : _graph.spriteSet())
                {
                    if (sprite.hasAttribute("ui.selected"))
                        sprite.removeAttribute("ui.selected");
                }
                notifySelectionChanged();
            }
        }

        _dragElement = null;
    }

    private void mouseButtonRelease(MouseEvent event)
    {
        _dragElement = null;
    }

    protected void selectElementsInArea(Iterable<GraphicElement> elementsInArea)
    {
        for (GraphicElement gsElement : elementsInArea)
        {
            GraphElement element = _viewManager.getBaseGraphElement(gsElement.getId());
            if (!element.isSelectable())
                continue;

            if (!gsElement.hasAttribute("ui.selected"))
            {
                gsElement.addAttribute("ui.selected");
            }
        }
        notifySelectionChanged();
    }

    protected void elementMoving(GraphicElement element, MouseEvent event)
    {
        _view.moveElementAtPx(element, event.getX(), event.getY());
    }

    protected void mouseButtonPressOnElement(GraphicElement element, MouseEvent event)
    {
        _view.freezeElement(element, true);

        if (event.getButton() == 1)
        {
            if (event.isShiftDown())
            {
                toggleSelection(element);
                notifySelectionChanged();
            }
            else
            {
                element.addAttribute("ui.clicked");
            }
        }
    }

    protected void mouseButtonReleaseFromElement(GraphicElement element, MouseEvent event)
    {
        _view.freezeElement(element, false);

        if (event.getButton() == 1)
        {
            if (element.hasAttribute("ui.clicked"))
            {
                element.removeAttribute("ui.clicked");
                notifyClickEnd(element);
            }
        }
    }

    private void toggleSelection(GraphicElement gsElement)
    {
        GraphElement element = _viewManager.getBaseGraphElement(gsElement.getId());
        if (!element.isSelectable())
            return;

        if (gsElement.hasAttribute("ui.selected"))
        {
            gsElement.removeAttribute("ui.selected");
        }
        else
        {
            gsElement.addAttribute("ui.selected");
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
        // NOP
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        mouseButtonPress(event);

        _touchedElement = _view.findNodeOrSpriteAt(event.getX(), event.getY());

        if (event.getButton() == 1)
            notifyClickBegin(_touchedElement);

        if (_touchedElement != null)
        {
            // TODO: consider using an attribute like "ui.fixed" to prevent possibility to move nodes
            if (((_touchedElement instanceof GraphicSprite) && _allowDragSprites) ||
                ((_touchedElement instanceof GraphicNode) && _allowDragNodes))
            {
                _dragElement = _touchedElement;
            }
            mouseButtonPressOnElement(_touchedElement, event);
        }
        else if (event.getButton() == 1)
        {
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

        if (event.getButton() == 1)
            notifyClickEnd(_touchedElement);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int rotation =  e.getWheelRotation();
        double currentZoom = _camera.getViewPercent();
        double zoomOffset = 0.1 * rotation * currentZoom;
        _camera.setViewPercent(currentZoom + zoomOffset);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // methods to retrieve current interaction state

    public HashSet<GraphElement> getCurrentSelection()
    {
        HashSet<GraphElement> currentSelection = new HashSet<>();
        for (Node node : _graph)
        {
            if (node.hasAttribute("ui.selected"))
            {
                GraphElement element = _viewManager.getBaseGraphElement(node.getId());
                currentSelection.add(element);
            }
        }
        for (GraphicSprite sprite : _graph.spriteSet())
        {
            if (sprite.hasAttribute("ui.selected"))
            {
                GraphElement element = _viewManager.getBaseGraphElement(sprite.getId());
                currentSelection.add(element);
            }
        }

        return currentSelection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IInteractionListener notification

    private void notifyClickBegin(GraphicElement gsElement)
    {
        GraphElement element = null;

        if (gsElement != null)
            element = _viewManager.getBaseGraphElement(gsElement.getId());

        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickBegin(element);
        }
    }

    private void notifyClickEnd(GraphicElement gsElement)
    {
        GraphElement element = null;

        if (gsElement != null)
            element = _viewManager.getBaseGraphElement(gsElement.getId());

        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickEnd(element);
        }
    }

    private HashSet<GraphElement> _lastSelection = new HashSet<>();
    private void notifySelectionChanged()
    {
        HashSet<GraphElement> newSelection = getCurrentSelection();

        HashSet<GraphElement> selected = new HashSet<>();
        HashSet<GraphElement> unselected = new HashSet<>();

        for (GraphElement e : _lastSelection)
        {
            if (!newSelection.contains(e))
                unselected.add(e);
        }
        for (GraphElement e : newSelection)
        {
            if (!_lastSelection.contains(e))
                selected.add(e);
        }

        _lastSelection = newSelection;

        if (selected.size() != 0 || unselected.size() != 0)
        {
            for(IInteractionListener listener : _interactionListeners)
            {
                listener.selectionChanged(selected, unselected);
            }
        }
    }

}
