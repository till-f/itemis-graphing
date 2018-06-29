package de.itemis.graphing.demo.handlers;

import de.itemis.graphing.view.interaction.ShowTooltipHoverHandler;
import de.itemis.graphing.model.GraphElement;

public class ShowLabelTooltipHoverHandler extends ShowTooltipHoverHandler {
    @Override
    public String getLabelText(GraphElement element, HoverParameters params) {
        if (element.getLabel() == null || element.getLabel().isEmpty())
            return null;

        return element.getLabel();
    }
}
