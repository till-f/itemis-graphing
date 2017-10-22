package de.itemis.graphing.example.handlers;

import de.itemis.graphing.model.FloatingAttachment;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.interaction.ShowAttachmentsHoverHandler;

import java.util.LinkedList;
import java.util.List;

public class ShowLabelTooltipHoverHandler extends ShowAttachmentsHoverHandler
{
    public ShowLabelTooltipHoverHandler()
    {
        super(0);
    }

    @Override
    protected List<AttachmentInfo> getAttachmentInfos(GraphElement element, HoverParameters params)
    {
        if (element.getLabel() == null || element.getLabel().isEmpty())
            return null;

        List<AttachmentInfo> ais = new LinkedList<>();
        Style s = new AttachmentStyle();
        s.setFillColor("FFFA90");
        s.setLineMode(Style.ELineMode.None);
        ais.add(new AttachmentInfo("tooltip", element.getLabel(), s));
        return ais;
    }

    @Override
    protected EPosition getPosition(GraphElement enterElement, HoverParameters params)
    {
        return EPosition.Top;
    }

    @Override
    protected void applyExtraAttributes(GraphElement element, HoverParameters params, FloatingAttachment attachment)
    {
        attachment.setIsSelectable(false);
        attachment.setIsClickable(false);
    }
}
