package de.itemis.graphing.interactionhandlers;

import de.itemis.graphing.model.*;
import de.itemis.graphing.view.IHoverHandler;
import de.itemis.graphing.view.IViewManager;

public class ShowTooltip implements IHoverHandler
{
    public enum ETooltipPosition { Top, Right, Bottom, Left }

    private IViewManager _viewManager = null;
    private FloatingAttachment _showingAttachment = null;
    private GraphElement _lastEnteredElement = null;

    @Override
    public void setViewManager(IViewManager viewManager)
    {
        _viewManager = viewManager;
    }

    @Override
    public void mouseHover(GraphElement enterElement, GraphElement exitElement, HoverParameters params)
    {
        if (_showingAttachment != null)
        {
            if (enterElement == _showingAttachment) return;
            if (_lastEnteredElement != null && enterElement == _lastEnteredElement) return;

            _showingAttachment.getParent().removeAttachment(_showingAttachment.getId());
        }
        _showingAttachment = null;
        _lastEnteredElement = enterElement;

        Vertex parent;
        if (enterElement instanceof Vertex)
        {
            parent = (Vertex) enterElement;
        }
        else if (enterElement instanceof AttachmentBase)
        {
            parent = ((AttachmentBase)enterElement).getParent();
        }
        else
        {
            return;
        }

        if (hasTooltip(enterElement, params))
        {
            String label = getLabel(enterElement, params);
            ETooltipPosition pos = getPosition(enterElement, params);

            Size size = calculateSize(label);

            FloatingAttachment attachment = parent.addFloatingAttachment(
                    getClass().getSimpleName(),
                    size.getWidth(),
                    size.getHeight(),
                    calculateAngle(pos),
                    calculateDistance(parent, size, pos),
                    FloatingAttachment.EPositioningMode.Radial,
                    true
                );

            attachment.setLabel(label);
            attachment.setLabelPrio(GraphElement.ELabelPriority.AlwaysShown);

            applyExtraAttributes(enterElement, params, attachment);

            _showingAttachment = attachment;
        }
    }

    public boolean hasTooltip(GraphElement element, HoverParameters params)
    {
        return element.getLabel() != null && !element.getLabel().isEmpty();
    }

    public String getLabel(GraphElement element, HoverParameters params)
    {
        return element.getLabel();
    }

    public ETooltipPosition getPosition(GraphElement enterElement, HoverParameters params)
    {
        return ETooltipPosition.Top;
    }

    public void applyExtraAttributes(GraphElement element, HoverParameters params, FloatingAttachment attachment)
    {
    }

    private Size calculateSize(String label)
    {
        Size txtSize = _viewManager.calculateTextSize(label);
        return new Size(txtSize.getWidth() + 10, txtSize.getHeight() + 4);
    }

    private double calculateAngle(ETooltipPosition position)
    {
        switch (position)
        {
            case Top:
                return 90;
            case Right:
                return 0;
            case Bottom:
                return 270;
            case Left:
                return 180;
            default:
                throw new RuntimeException("unexpected position: " + position);
        }
    }

    private double calculateDistance(Vertex parent, Size ownSize, ETooltipPosition position)
    {
        switch (position)
        {
            case Bottom:
            case Top:
                return parent.getSize().getHeight() / 2 + _viewManager.getGraphicalUnitsPerPixel() * ownSize.getHeight() / 2;

            case Right:
            case Left:
                return parent.getSize().getWidth() / 2 + _viewManager.getGraphicalUnitsPerPixel() * ownSize.getWidth() / 2;

            default:
                throw new RuntimeException("unexpected position: " + position);
        }
    }

}
