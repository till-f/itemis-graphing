package de.itemis.graphing.example.handlers;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.view.handlers.ShowTooltipBase;

public class ShowLabelTooltip extends ShowTooltipBase
{
    @Override
    protected boolean hasTooltip(GraphElement element, HoverParameters params)
    {
        return element.getLabel() != null && !element.getLabel().isEmpty();
    }

    @Override
    protected String getLabel(GraphElement element, HoverParameters params)
    {
        return element.getLabel();
    }
}
