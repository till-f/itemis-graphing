package de.itemis.graphing.model;

import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;

public abstract class AttachmentBase<T>  extends GraphElement<T> implements ISized
{
    private final Vertex<T> _parent;
    private final Size _size;
    private final boolean _affectDynamicLayout;

    private boolean _delegateInteractionToParent = false;

    public AttachmentBase(Vertex<T> vertex, String id, Size size, boolean affectDynamicLayout)
    {
        super(vertex.getGraph(), id);

        _parent = vertex;
        _size = size;
        _affectDynamicLayout = affectDynamicLayout;
        setIsSelectable(false);
    }

    @Override
    protected void setInitialStyles()
    {
        _styleRegular = AttachmentStyle.Default();
        _styleClicked = AttachmentStyle.Empty();
        _styleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleClicked.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleClicked.setIsInLevelForeground(true);
        _styleSelected = AttachmentStyle.Empty();
        _styleSelected.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleSelected.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleSelected.setIsInLevelForeground(true);
    }


    @Override
    public Size getSize()
    {
        return _size;
    }

    public Vertex<T> getParent()
    {
        return _parent;
    }

    public boolean isDynamicLayoutAffected()
    {
        return _affectDynamicLayout;
    }

    public boolean isDelegateInteractionToParent()
    {
        return _delegateInteractionToParent;
    }

    public void setDelegateInteractionToParent(boolean delegateInteractionToParent)
    {
        _delegateInteractionToParent = delegateInteractionToParent;
    }

}
