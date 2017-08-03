package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.Attachment;
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
import java.util.LinkedHashSet;

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
    protected final LinkedHashSet<IInteractionListener> _interactionListeners = new LinkedHashSet<IInteractionListener>();

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
                    interact(node.getId(), false, null, null, null);
                }

                for (GraphicSprite sprite : _graph.spriteSet())
                {
                    interact(sprite.getId(), false, null, null, null);
                }
                notifySelectionChanged();
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
            interact(element.getId(), true, null, null, null);
        }
        notifySelectionChanged();
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
            interact(gsElement.getId(), null, null, true, null);
        }
    }

    protected void mouseButtonReleaseFromElement(GraphicElement gsElement, MouseEvent event)
    {
        _view.freezeElement(gsElement, false);

        if (event.getButton() == 1)
        {
            interact(gsElement.getId(), null, null, null, true);

            if (event.isShiftDown())
            {
                interact(gsElement.getId(), null, true, null, null);
            }
            else
            {
                interact(gsElement.getId(), true, null, null, null);
            }
            notifySelectionChanged();
        }
    }

    private void interact(String elementId, Boolean select, Boolean toggleSelect, Boolean clickBegin, Boolean clickEnd)
    {
        GraphElement element = _viewManager.getGraphElement(elementId);
        if (element instanceof Attachment && ((Attachment) element).isDelegateInteractionToParent())
        {
            element = ((Attachment) element).getParent();
        }

        if (select != null)
        {
            if (element.isSelectable())
                element.setSelected(select);
        }
        else if (toggleSelect != null && toggleSelect)
        {
            if (element.isSelectable())
                element.setSelected(!element.isSelected());
        }
        else if (clickBegin != null && clickBegin)
        {
            element.clickBegin();
            notifyClickBegin(element);
        }
        else if (clickEnd != null && clickEnd)
        {
            element.clickEnd();
            notifyClickEnd(element);
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
            notifyClickBegin(null);

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
            notifyClickEnd(null);

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

    public LinkedHashSet<GraphElement> getCurrentSelection()
    {
        LinkedHashSet<GraphElement> currentSelection = new LinkedHashSet<>();
        for (Node node : _graph)
        {
            GraphElement element = _viewManager.getGraphElement(node.getId());
            if (element.isSelected())
                currentSelection.add(element);
        }
        for (GraphicSprite sprite : _graph.spriteSet())
        {
            GraphElement element = _viewManager.getGraphElement(sprite.getId());
            if (element.isSelected())
                currentSelection.add(element);
        }

        return currentSelection;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // IInteractionListener notification

    private void notifyClickBegin(GraphElement element)
    {
        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickBegin(element);
        }
    }

    private void notifyClickEnd(GraphElement element)
    {
        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickEnd(element);
        }
    }

    private LinkedHashSet<GraphElement> _lastSelection = new LinkedHashSet<>();
    private void notifySelectionChanged()
    {
        LinkedHashSet<GraphElement> newSelection = getCurrentSelection();

        LinkedHashSet<GraphElement> selected = new LinkedHashSet<>();
        LinkedHashSet<GraphElement> unselected = new LinkedHashSet<>();

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
