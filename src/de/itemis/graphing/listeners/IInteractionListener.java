package de.itemis.graphing.listeners;

import de.itemis.graphing.model.BaseGraphElement;

import java.util.Set;

public interface IInteractionListener
{

    /**
     * called when left mouse button is clicked.
     */
    void clickBegin(BaseGraphElement element);

    /**
     * called when left mouse button is released. element is ensured to be the same as last call to clickBegin().
     */
    void clickEnd(BaseGraphElement element);

    /**
     * called when the selection has changed. contains only changes since previous notifications.
     */
    void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected);
}
