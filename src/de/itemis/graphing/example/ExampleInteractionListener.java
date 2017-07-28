package de.itemis.graphing.example;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.IInteractionListener;

import java.util.Set;

public class ExampleInteractionListener implements IInteractionListener
{
    @Override
    public void elementClickStart(BaseGraphElement element)
    {
        System.out.println("INFO: Clicked element     : " + element.getId());
    }

    @Override
    public void elementClickEnd(BaseGraphElement element)
    {
        System.out.println("INFO: Released element    : " + element.getId());

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
