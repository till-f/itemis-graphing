package de.itemis.graphing.view.interaction;

import de.itemis.graphing.model.FloatingAttachment;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;

import java.util.LinkedList;
import java.util.List;

public abstract class ShowTooltipHoverHandler extends ShowAttachmentsHoverHandler
{
    public ShowTooltipHoverHandler()
    {
        super(0);
    }

    public abstract String getLabelText(GraphElement element, HoverParameters params);

    @Override
    protected List<AttachmentInfo> getAttachmentInfos(GraphElement element, HoverParameters params)
    {
        String txt = getLabelText(element, params);

        if (txt == null)
            return null;

        List<AttachmentInfo> ais = new LinkedList<>();
        Style s = AttachmentStyle.Default();
        s.setFillColor("FFFA90");
        s.setLineMode(Style.ELineMode.None);
        ais.add(new AttachmentInfo("tooltip", txt, s));
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
