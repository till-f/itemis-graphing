package de.itemis.graphing.view.interaction;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.util.Screen;
import de.itemis.graphing.view.IViewManager;

import java.util.LinkedList;
import java.util.List;

public abstract class ShowAttachmentsHoverHandler implements IHoverHandler
{

    public enum EPosition { Top, Right, Bottom, Left;}

    private final double SCALE = Screen.getScalingFactor();

    private final int _timeoutMS;
    private IViewManager _viewManager = null;
    private List<FloatingAttachment> _showingAttachments = new LinkedList<>();
    private GraphElement _showingElement = null;

    private Thread _removerThread = null;

    public ShowAttachmentsHoverHandler(int timeoutMS)
    {
        _timeoutMS = timeoutMS;
    }

    @Override
    public void setViewManager(IViewManager viewManager)
    {
        _viewManager = viewManager;
    }

    @Override
    public void mouseHover(GraphElement enterElement, GraphElement exitElement, HoverParameters params)
    {
        if (_showingElement != null)
        {
            if (_showingAttachments.contains(enterElement) || _showingElement == enterElement)
            {
                if (_removerThread != null)
                {
                    _removerThread.interrupt();
                    _removerThread = null;
                }
                return;
            }

            if (enterElement == null && _removerThread != null) return;

            if (_timeoutMS < 100)
            {
                resetAndRemove(_showingElement);
            }
            else
            {
                final GraphElement removerAssociatedElement = _showingElement;
                _removerThread = new Thread(() -> {
                    try
                    {
                        Thread.sleep(1000);
                        resetAndRemove(removerAssociatedElement);
                    }
                    catch (InterruptedException e)
                    {
                    }
                });
                _removerThread.start();
            }
        }

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

        List<AttachmentInfo> ais = getAttachmentInfos(enterElement, params);
        if (ais == null || ais.size() <= 0)
            return;

        if (_removerThread != null)
        {
            _removerThread.interrupt();
            while (_removerThread.isAlive());
            _removerThread = null;
            resetAndRemove(_showingElement);
        }

        calculateSizeAndPosition(
                ais,
                enterElement,
                params,
                parent
        );

        for (AttachmentInfo ai : ais)
        {
            FloatingAttachment attachment = parent.addFloatingAttachment(
                    ai.getId(),
                    ai.pixelSize.getWidth(),
                    ai.pixelSize.getHeight(),
                    ai.x,
                    ai.y,
                    FloatingAttachment.EPositioningMode.XY,
                    true
            );

            attachment.setLabel(ai.getLabel());
            attachment.setStyleRegular(ai.getStyle());
            attachment.setLabelPrio(GraphElement.ELabelPriority.AlwaysShown);

            applyExtraAttributes(enterElement, params, attachment);

            _showingElement = enterElement;
            _showingAttachments.add(attachment);
        }
    }

    protected abstract List<AttachmentInfo> getAttachmentInfos(GraphElement element, HoverParameters params);

    protected EPosition getPosition(GraphElement enterElement, HoverParameters params)
    {
        return EPosition.Bottom;
    }

    protected double getSpacing(GraphElement enterElement, HoverParameters params)
    {
        return 4;
    }

    protected void applyExtraAttributes(GraphElement element, HoverParameters params, FloatingAttachment attachment)
    {
        attachment.setIsSelectable(false);
    }

    private void calculateSizeAndPosition(List<AttachmentInfo> ais, GraphElement enterElement, HoverParameters params, Vertex parent)
    {
        EPosition pos = getPosition(enterElement, params);
        double spacing = getSpacing(enterElement, params) * SCALE;

        double gpp = _viewManager.getGraphicalUnitsPerPixel();
        spacing = spacing * gpp;

        double maxWidth = 0.0;
        double maxHeight = 0.0;
        double sumWidth = 0.0;
        double sumHeight = 0.0;
        boolean first = true;
        for(AttachmentInfo ai : ais)
        {
            if (!first)
            {
                sumWidth += spacing;
                sumHeight += spacing;
            }

            Size pixelSize = calculatePixelSize(ai.getLabel(), ai.getPadding(), ai.getStyle());
            Size guSize = pixelSize.scale(gpp);

            ai.pixelSize = pixelSize;
            ai.guSize = guSize;

            maxWidth = Math.max(maxWidth, guSize.getWidth());
            maxHeight = Math.max(maxHeight, guSize.getHeight());
            sumWidth += guSize.getWidth();
            sumHeight += guSize.getHeight();

            first = false;
        }

        double offsWidth = -sumWidth / 2;
        double offsHeight = -sumHeight / 2;
        for(AttachmentInfo ai : ais)
        {
            switch (pos)
            {
                case Top:
                case Bottom:
                    ai.x = offsWidth + ai.guSize.getWidth() / 2;
                    ai.y = ((maxHeight + parent.getSize().getHeight()) / 2 + spacing) * ((pos == EPosition.Bottom) ? -1 : 1);
                    offsWidth += ai.guSize.getWidth() + spacing;
                    break;
                case Right:
                case Left:
                    ai.x = ((ai.guSize.getWidth() + parent.getSize().getWidth()) / 2 + spacing) * ((pos == EPosition.Left) ? -1 : 1);
                    ai.y = offsHeight + ai.guSize.getHeight() / 2;
                    offsHeight += ai.guSize.getHeight() + spacing;
                    break;
            }
        }
    }

    private Size calculatePixelSize(String label, Padding p, Style s)
    {
        Size txtSize = _viewManager.calculateTextSize(label, s);
        return txtSize.addPadding(p.scale(SCALE));
    }

    private void resetAndRemove(GraphElement associatedElement)
    {
        synchronized (this)
        {
            if (_showingElement != null && _showingElement == associatedElement)
            {
                for (FloatingAttachment att : _showingAttachments)
                {
                    att.getParent().removeAttachment(att.getId());
                }
                _showingAttachments.clear();
                _showingElement = null;
            }
        }
    }

    public static class AttachmentInfo
    {
        private final String id;
        private final String label;

        private final Padding padding;

        private final Style style;
        private Size pixelSize = null;

        private Size guSize = null;
        private double x = 0.0;
        private double y = 0.0;
        public AttachmentInfo(String id, String label)
        {
            this(id, label, new Padding(10, 4), AttachmentStyle.Default());
        }

        public AttachmentInfo(String id, String label, Padding padding)
        {
            this(id, label, padding, AttachmentStyle.Default());
        }

        public AttachmentInfo(String id, String label, Style style)
        {
            this(id, label, new Padding(10, 4), style);
        }

        public AttachmentInfo(String id, String label, Padding padding, Style style)
        {
            this.id = id;
            this.label = label;
            this.padding = padding;
            this.style = style;
        }

        public String getId()
        {
            return id;
        }

        public String getLabel()
        {
            return label;
        }

        public Padding getPadding()
        {
            return padding;
        }

        public Style getStyle()
        {
            return style;
        }
    }
}
