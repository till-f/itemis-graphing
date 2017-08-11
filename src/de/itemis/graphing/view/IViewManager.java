package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Set;

public interface IViewManager
{
    // -----------------------------------------------------------------------------------------------------------------
    // rendering technology specific (must be implemented by custom view managers)

    JPanel getView();

    void zoomIn();

    void zoomOut();

    void fitView();

    void close();

    // -----------------------------------------------------------------------------------------------------------------
    // not technology specific (default implementation in AbstractViewManager should not be overridden)

    Set<GraphElement> getSelectedElements();

    void registerInteractionListener(IInteractionListener listener);

    void removeInteractionListener(IInteractionListener listener);

    /**
     * Must be called by the interaction handling component (e.g. mouse controller) when an element is clicked/selected.
     * Calls notifyClickBegin() and notifyClickEnd(), but NOT notifySelectionChanged() in order to support comprehensive
     * selection changed notifications.
     */
    void applyInteraction(String elementId, Boolean select, Boolean toggleSelect, Boolean clickBegin, Boolean clickEnd, MouseEvent event);

    /**
     * Should be called by the interaction handling component only on clicks in empty space (element == null),
     * as it is automatically called from applyInteraction() above.
     */
    void notifyClickBegin(GraphElement element, MouseEvent event);

    /**
     * Should be called by the interaction handling component only on clicks in empty space (element == null),
     * as it is automatically called from applyInteraction() above.
     */
    void notifyClickEnd(GraphElement element, MouseEvent event);

    /**
     * Must always be called by the interaction handling component after the selection has changed.
     */
    void notifySelectionChanged();
}
