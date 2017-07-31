package de.itemis.graphing.listeners;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.BlockStyle;

import java.util.Set;

public class ShowDeleteButton implements IInteractionListener
{
    private final AttachmentStyle regularStyle = new AttachmentStyle();
    private final AttachmentStyle clickedStyle;

    private Attachment _deleteAttachment = null;

    public ShowDeleteButton()
    {
        regularStyle.setShape(BlockStyle.EShape.Circle);
        regularStyle.setFillColor("F4D2D2");

        clickedStyle = (AttachmentStyle) regularStyle.getCopy();
        clickedStyle.setLineColor("AA0000");
        clickedStyle.setLineThickness(Graph.DEFAULT_HL_LINE_THICKNESS);
    }

    @Override
    public void clickBegin(BaseGraphElement element)
    {
        if (_deleteAttachment != null && (_deleteAttachment != element || element == null))
            _deleteAttachment.getParent().removeAttachment(_deleteAttachment.getId());
    }

    @Override
    public void clickEnd(BaseGraphElement element)
    {
        if (element instanceof Vertex)
        {
            Vertex vertex = (Vertex) element;
            _deleteAttachment = vertex.addAttachment(element.getId() + "_delete", 0.2, 0.2, 0.05, Attachment.ELocation.West);
            _deleteAttachment.setLabel("-");
            _deleteAttachment.setStyle(IStyled.EStyle.Regular, regularStyle);
            _deleteAttachment.setStyle(IStyled.EStyle.Clicked, clickedStyle);
        }
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
    }
}
