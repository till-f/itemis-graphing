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
    // not rendering technology specific (default implementation in AbstractViewManager should not be overridden)

    Set<GraphElement> getSelectedElements();

    void registerHoverHandler(IHoverHandler handler);

    void removeHoverHandler(IHoverHandler handler);

    void registerClickHandler(IClickHandler handler);

    void removeClickHandler(IClickHandler handler);

    void registerSelectionHandler(ISelectionHandler handler);

    void removeSelectionHandler(ISelectionHandler handler);

    /**
     * Must be called by the interaction handling component (e.g. mouse controller) when the mouse is entering / exiting.
     * elementId must not be null.
     */
    void applyHoverInteraction(String elementId, boolean mouseEntered, MouseEvent event);

    /**
     * Must be called by the interaction handling component (e.g. mouse controller) when an element is clicked.
     * elementId may be null to indicate clicks in empty space.
     */
    void applyClickInteraction(String elementId, boolean clickBegin, MouseEvent event);

    /**
     * Must be called by the interaction handling component (e.g. mouse controller) when an element is selected or unselected.
     * select may be null to indicate that the selection is toggled.
     * elementId must not be null.
     * A subsequent call to selectionCompleted() is necessary in every case (allows to comprehend multiple selected elements).
     */
    void applySelectInteraction(String elementId, Boolean select);

    /**
     * Must always be called by the interaction handling component after the selection has changed.
     */
    void selectionCompleted();
}
