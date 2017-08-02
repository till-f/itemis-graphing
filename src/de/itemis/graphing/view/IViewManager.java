package de.itemis.graphing.view;

import de.itemis.graphing.model.GraphElement;

import javax.swing.JPanel;
import java.util.Set;

public interface IViewManager
{
    void zoomIn();

    void zoomOut();

    void fitView();

    void setShowLabels(boolean show);

    Set<GraphElement> getSelectedElements();

    JPanel getView();
}
