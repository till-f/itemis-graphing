package de.itemis.graphing.view.handlers;

import de.itemis.graphing.model.*;
import de.itemis.graphing.util.Screen;
import de.itemis.graphing.view.IViewManager;

public abstract class ShowTooltipBase implements IHoverHandler
{


    public enum ETooltipPosition { Top, Right, Bottom, Left;}

    private final double SCALE = Screen.getScalingFactor();

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

            Size size = calculateSize(label, getPadding(enterElement, params));

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

    protected abstract boolean hasTooltip(GraphElement element, HoverParameters params);

    protected abstract String getLabel(GraphElement element, HoverParameters params);

    protected ETooltipPosition getPosition(GraphElement enterElement, HoverParameters params)
    {
        return ETooltipPosition.Top;
    }

    protected Padding getPadding(GraphElement enterElement, HoverParameters params)
    {
        return new Padding(10, 4);
    }

    protected void applyExtraAttributes(GraphElement element, HoverParameters params, FloatingAttachment attachment)
    {
        attachment.setIsSelectable(false);
        attachment.setIsClickable(false);
    }

    protected Size calculateSize(String label, Padding p)
    {
        Size txtSize = _viewManager.calculateTextSize(label);
        return txtSize.addPadding(p.scale(SCALE));
    }

    protected double calculateAngle(ETooltipPosition position)
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

    protected double calculateDistance(Vertex parent, Size ownSize, ETooltipPosition position)
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
