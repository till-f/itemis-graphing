package de.itemis.graphing.util;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.IStyled;
import de.itemis.graphing.model.IViewListener;

import java.util.Set;

public class StylingViewListener implements IViewListener
{
    @Override
    public void elementClickStart(BaseGraphElement element)
    {
        element.selectActiveStyle(IStyled.EStyle.Clicked);
    }

    @Override
    public void elementClickEnd(BaseGraphElement element)
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
