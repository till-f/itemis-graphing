package de.itemis.graphing.view;

import de.itemis.graphing.model.AttachmentBase;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.Size;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.interaction.IClickHandler;
import de.itemis.graphing.view.interaction.IHoverHandler;
import de.itemis.graphing.view.interaction.IInteractionHandler;
import de.itemis.graphing.view.interaction.ISelectionHandler;

import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractViewManager<T> implements IViewManager<T>
{
    private final LinkedHashSet<IHoverHandler> _hoverHandlers = new LinkedHashSet<>();
    private final LinkedHashSet<IClickHandler> _clickHandlers = new LinkedHashSet<>();
    private final LinkedHashSet<ISelectionHandler> _selectionHandlers = new LinkedHashSet<>();
    private final Graph<T> _graph;

    private Set<GraphElement<T>> _lastSelection = new LinkedHashSet<>();

    public AbstractViewManager(Graph<T> graph)
    {
        _graph = graph;
    }

    @Override
    public Graph<T> getGraph()
    {
        return _graph;
    }

    @Override
    public void registerHandler(IInteractionHandler handler)
    {
        if (handler instanceof IHoverHandler)
        {
            _hoverHandlers.add((IHoverHandler) handler);
        }
        if (handler instanceof IClickHandler)
        {
            _clickHandlers.add((IClickHandler) handler);
        }
        if (handler instanceof ISelectionHandler)
        {
            _selectionHandlers.add((ISelectionHandler) handler);
        }

        handler.setViewManager(this);
    }

    @Override
    public void removeHandler(IInteractionHandler handler)
    {
        if (handler instanceof IHoverHandler)
        {
            _hoverHandlers.remove(handler);
        }
        if (handler instanceof IClickHandler)
        {
            _clickHandlers.remove(handler);
        }
        if (handler instanceof ISelectionHandler)
        {
            _selectionHandlers.remove(handler);
        }
    }

    @Override
    public boolean isMultiSelectHotkey(MouseEvent e)
    {
        return e.isShiftDown();
    }

    @Override
    public Set<GraphElement<T>> getSelectedElements()
    {
        LinkedHashSet<GraphElement<T>> currentSelection = new LinkedHashSet<>();
        for (GraphElement<T> ge : _graph.getVertexes())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        for (GraphElement<T> ge : _graph.getEdges())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        for (GraphElement<T> ge : _graph.getAttachments())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        return currentSelection;
    }

    // subject to refactoring
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public double getGraphicalUnitsPerPixel() {
        return 1.0;
    }

    @Override
    public Size calculateTextSize(String label, Style s) {
        return new Size(1.0, 1.0);
    }


    // Interaction handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void applyHoverInteraction(String enterId, String exitId, MouseEvent event)
    {
        GraphElement enterElement = enterId == null ? null : getInteractionElement(enterId);
        GraphElement exitElement = exitId == null ? null : getInteractionElement(exitId);

        // notify listeners
        IHoverHandler.HoverParameters params = new IHoverHandler.HoverParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown());
        for(IHoverHandler listener : _hoverHandlers)
        {
            listener.mouseHover(enterElement, exitElement, params);
        }
    }

    @Override
    public void applyClickInteraction(String elementId, boolean clickBegin, MouseEvent event)
    {
        if (event.getButton() == 1 && !isMultiSelectHotkey(event))
        {
            for(GraphElement<?> element : getSelectedElements())
            {
                applySelectInteraction(element.getId(), false);
            }
            selectionCompleted();
        }

        GraphElement element = elementId == null ? null : getInteractionElement(elementId);

        if (element != null)
        {
            if (!element.isClickable())
                return;

            element.setClicked(clickBegin);
        }

        // notify listeners
        IClickHandler.ClickParameters.EButton button;
        switch (event.getButton())
        {
            case 1:
                button = IClickHandler.ClickParameters.EButton.LEFT;
                break;
            case 2:
                button = IClickHandler.ClickParameters.EButton.MIDDLE;
                break;
            case 3:
                button = IClickHandler.ClickParameters.EButton.RIGHT;
                break;
            default:
                button = IClickHandler.ClickParameters.EButton.OTHER;
        }
        IClickHandler.ClickParameters params = new IClickHandler.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown(), button);
        for(IClickHandler listener : _clickHandlers)
        {
            if (clickBegin)
            {
                listener.clickBegin(element, params);
            }
            else
            {
                listener.clickEnd(element, params);
            }
        }
    }


    @Override
    public void applySelectInteraction(String elementId, Boolean select)
    {
        GraphElement element = getInteractionElement(elementId);

        if (element == null)
            return;

        if (select == null)
        {
            if (element.isSelectable())
                element.setSelected(!element.isSelected());
        }
        else
        {
            if (element.isSelectable())
                element.setSelected(select);
        }
    }

    @Override
    public void selectionCompleted()
    {
        Set<GraphElement<T>> newSelection = getSelectedElements();

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
            for(ISelectionHandler listener : _selectionHandlers)
            {
                listener.selectionChanged(selected, unselected);
            }
        }
    }

    private GraphElement getInteractionElement(String elementId)
    {
        GraphElement element = _graph.getElement(elementId);

        if (element == null)
            return null;

        if (element instanceof AttachmentBase && ((AttachmentBase) element).isDelegateInteractionToParent())
        {
            element = ((AttachmentBase) element).getParent();
        }

        return element;
    }
}
