package de.itemis.graphing.interactions;

import de.itemis.graphing.model.*;
import de.itemis.graphing.view.IHoverHandler;
import de.itemis.graphing.view.IViewManager;

public class ShowTooltip implements IHoverHandler
{
    private IViewManager _viewManager = null;
    private FloatingAttachment _showingAttachment = null;

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
            if (enterElement == _showingAttachment)
            {
                return;
            }
            else
            {
                _showingAttachment.getParent().removeAttachment(_showingAttachment.getId());
                _showingAttachment = null;
            }
        }

        if (showTooltip(enterElement, params))
        {
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

            Size size = getSize(enterElement, params);

            FloatingAttachment a = parent.addFloatingAttachment(
                    getClass().getSimpleName(),
                    size.getWidth(),
                    size.getHeight(),
                    getAngleOrX(enterElement, params),
                    getDistanceOrY(enterElement, params),
                    getPosMode(enterElement, params),
                    isPixelCoordinates(enterElement, params)
                );

            applyProperties(enterElement, params, a);
            _showingAttachment = a;
        }
    }

    public boolean showTooltip(GraphElement element, HoverParameters params)
    {
        return true;
    }

    public Size getSize(GraphElement element, HoverParameters params)
    {
        return new Size(_viewManager.calculateTextSize(element.getLabel() + 10), 20);
    }

    public double getAngleOrX(GraphElement element, HoverParameters params)
    {
        return 90.0;
    }

    public double getDistanceOrY(GraphElement element, HoverParameters params)
    {
        return 15;
    }

    public FloatingAttachment.EPositioningMode getPosMode(GraphElement element, HoverParameters params)
    {
        return FloatingAttachment.EPositioningMode.Radial;
    }

    public boolean isPixelCoordinates(GraphElement element, HoverParameters params)
    {
        return true;
    }

    public void applyProperties(GraphElement element, HoverParameters params, FloatingAttachment a)
    {
        a.setLabel(element.getLabel());
        a.setLabelPrio(GraphElement.ELabelPriority.AlwaysShown);
    }
}
