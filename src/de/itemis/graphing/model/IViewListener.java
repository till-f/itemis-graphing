package de.itemis.graphing.model;

import java.util.Set;

public interface IViewListener
{
    void elementClicked(BaseGraphElement element);
    void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected);
}
