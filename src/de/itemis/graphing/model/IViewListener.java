package de.itemis.graphing.model;

import java.util.Set;

public interface IViewListener
{
    void elementClickStart(BaseGraphElement element);

    void elementClickEnd(BaseGraphElement element);

    void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected);
}
