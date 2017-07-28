package de.itemis.graphing.listeners;

import de.itemis.graphing.model.BaseGraphElement;

import java.util.Set;

public interface IInteractionListener
{

    /**
     * called when left mouse button is clicked somewhere.
     */
    void clickBegin();

    /**
     * called when left mouse button is clicked on element. clickBegin() has been called before in this case.
     */
    void clickBegin(BaseGraphElement element);

    /**
     * called when left mouse button is released after element has been clicked.
     */
    void clickEnd(BaseGraphElement element);

    /**
     * called when the selection has changed. contains only changes since previous notifications.
     */
    void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected);
}
