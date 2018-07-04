package de.itemis.graphing.view;

import de.itemis.graphing.model.Graph;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.Size;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.interaction.IInteractionHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Set;

public interface IViewManager<T>
{
    // -----------------------------------------------------------------------------------------------------------------
    // rendering technology specific (must be implemented by custom view managers)

    Component getView();

    void zoomIn();

    void zoomOut();

    void fitView();

    void close();

    // -----------------------------------------------------------------------------------------------------------------
    // not rendering technology specific (default implementation in AbstractViewManager should not be overridden)

    Graph<T> getGraph();

    Set<GraphElement<T>> getSelectedElements();

    void registerHandler(IInteractionHandler handler);

    void removeHandler(IInteractionHandler handler);

    boolean isMultiSelectHotkey(MouseEvent e);

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

    // -----------------------------------------------------------------------------------------------------------------
    // subject to refactoring

    /**
     * This method should not be part of the interface, but is used to calculate the size for certain attachments. This
     * should be done internally. TODO: consider to extend / adapt the interface to add attachments
     */
    double getGraphicalUnitsPerPixel();

    /**
     * This method should not be part of the interface, but is used to calculate the size for certain attachments. This
     * should be done internally. TODO: consider to extend / adapt the interface to add attachments
     */
    Size calculateTextSize(String label, Style s);

}
