package de.itemis.graphing.view;

import de.itemis.graphing.model.BaseGraphElement;

import javax.swing.JPanel;
import java.util.Set;

public interface IViewManager
{
    void zoomIn();

    void zoomOut();

    void fitView();

    void relayout();

    Set<BaseGraphElement> getSelectedElements();

    JPanel getView();
}
