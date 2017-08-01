package de.itemis.graphing.listeners;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.listeners.IInteractionListener;

import java.util.Set;

public class DebugInteractionListener implements IInteractionListener
{
    @Override
    public void clickBegin(GraphElement element)
    {
        String id = "";
        if (element != null)
            id = element.getId();

        System.out.println("INFO: Clicked element     : " + id);
    }

    @Override
    public void clickEnd(GraphElement element)
    {
        String id = "";
        if (element != null)
            id = element.getId();

        System.out.println("INFO: Released element    : " + id);
    }

    @Override
    public void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected)
    {
        System.out.print("INFO: Selected elements   : ");
        for(GraphElement element: selected)
        {
            System.out.print(element.getId() + ", ");
        }

        System.out.println();

        System.out.print("INFO: Unselected elements : ");
        for(GraphElement element: unselected)
        {
            System.out.print(element.getId() + ", ");
        }

        System.out.println();
    }
}
