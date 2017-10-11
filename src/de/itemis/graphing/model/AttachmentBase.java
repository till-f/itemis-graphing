package de.itemis.graphing.model;

import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;

public abstract class AttachmentBase  extends GraphElement implements ISized
{
    private final Vertex _parent;
    private final Size _size;
    private final boolean _affectDynamicLayout;

    public AttachmentBase(Vertex vertex, String id, Size size, boolean affectDynamicLayout)
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
        _styleRegular = new AttachmentStyle();
        _styleClicked = new AttachmentStyle();
        _styleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleClicked.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleClicked.setIsInLevelForeground(true);
        _styleSelected = new AttachmentStyle();
        _styleSelected.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleSelected.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleSelected.setIsInLevelForeground(true);
    }


    @Override
    public Size getSize()
    {
        return _size;
    }

    public Vertex getParent()
    {
        return _parent;
    }

    public boolean isDynamicLayoutAffected()
    {
        return _affectDynamicLayout;
    }

}
