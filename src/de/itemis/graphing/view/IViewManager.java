package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.Size;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.interaction.IInteractionHandler;

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

    double getGraphicalUnitsPerPixel();

    Size calculateTextSize(String txt, Style style);

    // -----------------------------------------------------------------------------------------------------------------
    // not rendering technology specific (default implementation in AbstractViewManager should not be overridden)

    Set<GraphElement> getSelectedElements();

    void registerHandler(IInteractionHandler handler);

    void removeHandler(IInteractionHandler handler);

    /**
     * Must be called by the interaction handling component (e.g. mouse controller) when the mouse is entering / exiting.
     * one of enterId, exitId may be null to indicate enter/exit from/to empty space.
     */
    void applyHoverInteraction(String enterId, String exitId, MouseEvent event);

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
