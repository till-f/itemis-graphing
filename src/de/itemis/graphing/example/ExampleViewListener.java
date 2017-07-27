package de.itemis.graphing.example;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.IViewListener;

import java.util.List;
import java.util.Set;

public class ExampleViewListener implements IViewListener
{
    @Override
    public void elementClicked(BaseGraphElement element)
    {
        System.out.println("INFO: Element '" + element.getId() + "' clicked.");
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
        System.out.println("INFO: Selection changed.");
        System.out.println("  New selected elements: ");
        for(BaseGraphElement element: selected)
        {
            System.out.println("    " + element.getId());
        }

        System.out.println("  Unselected elements: ");
        for(BaseGraphElement element: unselected)
        {
            System.out.println("    " + element.getId());
        }
    }
}
