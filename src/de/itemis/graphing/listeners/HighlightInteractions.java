package de.itemis.graphing.listeners;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.IStyled;

import java.util.Set;

public class HighlightInteractions implements IInteractionListener
{
    @Override
    public void clickBegin(GraphElement element)
    {
        if(element != null)
            element.selectActiveStyle(IStyled.EStyle.Clicked);
    }

    @Override
    public void clickEnd(GraphElement element)
    {
        if(element != null)
            element.selectActiveStyle(IStyled.EStyle.Regular);
    }

    @Override
    public void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected)
    {
        for(GraphElement element: selected)
        {
            element.selectActiveStyle(IStyled.EStyle.Selected);
        }

        for(GraphElement element: unselected)
        {
            element.selectActiveStyle(IStyled.EStyle.Regular);
        }
    }
}
