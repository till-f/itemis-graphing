package de.itemis.graphing.view.interaction;

import de.itemis.graphing.model.GraphElement;

import java.util.Set;

public interface ISelectionHandler extends IInteractionHandler
{

    /**
     * called when the selection has changed. contains changes since previous notifications only.
     */
    void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected);

}
