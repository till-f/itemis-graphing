package de.itemis.graphing.view;

import de.itemis.graphing.model.AttachmentBase;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.GraphElement;

import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractViewManager implements IViewManager
{
    private final LinkedHashSet<IHoverHandler> _hoverHandlers = new LinkedHashSet<>();
    private final LinkedHashSet<IClickHandler> _clickHandlers = new LinkedHashSet<>();
    private final LinkedHashSet<ISelectionHandler> _selectionHandlers = new LinkedHashSet<>();
    private final Graph _graph;

    private Set<GraphElement> _lastSelection = new LinkedHashSet<>();

    public AbstractViewManager(Graph graph)
    {
        _graph = graph;
    }

    @Override
    public void registerHandler(IHandler handler)
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
    public void removeHandler(IHandler handler)
    {
        if (handler instanceof IHoverHandler)
        {
            _hoverHandlers.remove((IHoverHandler) handler);
        }
        if (handler instanceof IClickHandler)
        {
            _clickHandlers.remove((IClickHandler) handler);
        }
        if (handler instanceof ISelectionHandler)
        {
            _selectionHandlers.remove((ISelectionHandler) handler);
        }
    }

    @Override
    public Set<GraphElement> getSelectedElements()
    {
        LinkedHashSet<GraphElement> currentSelection = new LinkedHashSet<>();
        for (GraphElement ge : _graph.getVertexes())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        for (GraphElement ge : _graph.getEdges())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        for (GraphElement ge : _graph.getAttachments())
        {
            if (ge.isSelected())
                currentSelection.add(ge);
        }
        return currentSelection;
    }

    // Interaction handling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void applyHoverInteraction(String enterId, String exitId, MouseEvent event)
    {
        GraphElement enterElement = enterId == null ? null : getInteractionElement(enterId);
        GraphElement exitElement = exitId == null ? null :getInteractionElement(exitId);

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
        GraphElement element = elementId == null ? null : getInteractionElement(elementId);

        if (element != null)
        {
            element.setClicked(clickBegin);
        }

        // notify listeners
        IClickHandler.ClickParameters params = new IClickHandler.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown());
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
        Set<GraphElement> newSelection = getSelectedElements();

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
            throw new RuntimeException("illegal call to applyInteraction: could not get element for id: " + elementId);

        if (element instanceof AttachmentBase && ((AttachmentBase) element).isDelegateInteractionToParent())
        {
            element = ((AttachmentBase) element).getParent();
        }

        return element;
    }
}
