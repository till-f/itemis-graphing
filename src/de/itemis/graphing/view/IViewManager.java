package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Set;

public interface IViewManager
{
    // -----------------------------------------------------------------------------------------------------------------
    // rendering technology specific

    JPanel getView();

    void zoomIn();

    void zoomOut();

    void fitView();

    void close();

    // -----------------------------------------------------------------------------------------------------------------
    // not technology specific

    Set<GraphElement> getSelectedElements();

    void registerInteractionListener(IInteractionListener listener);

    void removeInteractionListener(IInteractionListener listener);

    void applyInteraction(String elementId, Boolean select, Boolean toggleSelect, Boolean clickBegin, Boolean clickEnd, MouseEvent event);
}
