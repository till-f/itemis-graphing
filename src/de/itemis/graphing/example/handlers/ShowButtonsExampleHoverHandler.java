package de.itemis.graphing.example.handlers;

import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.view.handlers.ShowAttachmentsHoverHandler;

import java.util.LinkedList;
import java.util.List;

public class ShowButtonsExampleHoverHandler extends ShowAttachmentsHoverHandler
{
    public ShowButtonsExampleHoverHandler()
    {
        super(1000);
    }

    @Override
    protected List<AttachmentInfo> getAttachmentInfos(GraphElement element, HoverParameters params)
    {
        List<AttachmentInfo> ais = new LinkedList<>();

        if (element.getLabel() != null && !element.getLabel().isEmpty())
        {
            ais.add(new AttachmentInfo("label", element.getLabel()));
        }
        ais.add(new AttachmentInfo("hello", "hello"));
        ais.add(new AttachmentInfo("world", "world"));
        return ais;
    }
}
