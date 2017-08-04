package de.itemis.graphing.view;

import de.itemis.graphing.model.Attachment;
import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.GraphElement;

import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractViewManager implements IViewManager
{
    private final LinkedHashSet<IInteractionListener> _interactionListeners = new LinkedHashSet<IInteractionListener>();
    private final Graph _graph;

    public AbstractViewManager(Graph graph)
    {
        _graph = graph;
    }

    @Override
    public void registerInteractionListener(IInteractionListener listener)
    {
        _interactionListeners.add(listener);
    }

    @Override
    public void removeInteractionListener(IInteractionListener listener)
    {
        _interactionListeners.remove(listener);
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
    public void applyInteraction(String elementId, Boolean select, Boolean toggleSelect, Boolean clickBegin, Boolean clickEnd, MouseEvent event)
    {
        GraphElement element = _graph.getElement(elementId);
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
            notifyClickBegin(element, new IInteractionListener.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown()));
        }
        else if (clickEnd != null && clickEnd)
        {
            element.clickEnd();
            notifyClickEnd(element, new IInteractionListener.ClickParameters(event.isControlDown(), event.isShiftDown(), event.isAltDown()));
        }
    }

    public void notifyClickBegin(GraphElement element, IInteractionListener.ClickParameters params)
    {
        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickBegin(element, params);
        }
    }

    public void notifyClickEnd(GraphElement element, IInteractionListener.ClickParameters params)
    {
        for(IInteractionListener listener : _interactionListeners)
        {
            listener.clickEnd(element, params);
        }
    }

    private Set<GraphElement> _lastSelection = new LinkedHashSet<>();
    public void notifySelectionChanged()
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
            for(IInteractionListener listener : _interactionListeners)
            {
                listener.selectionChanged(selected, unselected);
            }
        }
    }
}
