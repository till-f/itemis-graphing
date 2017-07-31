package de.itemis.graphing.example;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.listeners.IInteractionListener;

import java.util.Set;

public class DebugInteractionListener implements IInteractionListener
{
    @Override
    public void clickBegin(BaseGraphElement element)
    {
        String id = "";
        if (element != null)
            id = element.getId();

        System.out.println("INFO: Clicked element     : " + id);
    }

    @Override
    public void clickEnd(BaseGraphElement element)
    {
        String id = "";
        if (element != null)
            id = element.getId();

        System.out.println("INFO: Released element    : " + id);
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
        System.out.print("INFO: Selected elements   : ");
        for(BaseGraphElement element: selected)
        {
            System.out.print(element.getId() + ", ");
        }

        System.out.println();

        System.out.print("INFO: Unselected elements : ");
        for(BaseGraphElement element: unselected)
        {
            System.out.print(element.getId() + ", ");
        }

        System.out.println();
    }
}
