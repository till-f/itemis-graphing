package de.itemis.graphing.view;

import javax.swing.JPanel;

public interface IViewManager
{
    void zoomIn();

    void zoomOut();

    void fitView();

    JPanel getView();
}
