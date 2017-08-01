package de.itemis.graphing.listeners;

import de.itemis.graphing.model.GraphElement;

import java.util.Set;

public interface IInteractionListener
{

    /**
     * called when left mouse button is clicked.
     */
    void clickBegin(GraphElement element);

    /**
     * called when left mouse button is released. element is ensured to be the same as last call to clickBegin().
     */
    void clickEnd(GraphElement element);

    /**
     * called when the selection has changed. contains only changes since previous notifications.
     */
    void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected);
}
