package de.itemis.graphing.listeners;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.IStyled;

import java.util.Set;

public class HighlightInteractions implements IInteractionListener
{
    @Override
    public void clickBegin()
    {
    }

    @Override
    public void clickBegin(BaseGraphElement element)
    {
        element.selectActiveStyle(IStyled.EStyle.Clicked);
    }

    @Override
    public void clickEnd(BaseGraphElement element)
    {
        element.selectActiveStyle(IStyled.EStyle.Regular);
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
        for(BaseGraphElement element: selected)
        {
            element.selectActiveStyle(IStyled.EStyle.Selected);
        }

        for(BaseGraphElement element: unselected)
        {
            element.selectActiveStyle(IStyled.EStyle.Regular);
        }
    }
}
