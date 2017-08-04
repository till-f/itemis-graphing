package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.util.Set;

public interface IViewManager
{
    JPanel getView();

    void zoomIn();

    void zoomOut();

    void fitView();

    void setShowLabels(boolean show);

    Set<GraphElement> getSelectedElements();

    void registerInteractionListener(IInteractionListener listener);

    void removeInteractionListener(IInteractionListener listener);

    void applyInteraction(String elementId, Boolean select, Boolean toggleSelect, Boolean clickBegin, Boolean clickEnd, MouseEvent event);
}
