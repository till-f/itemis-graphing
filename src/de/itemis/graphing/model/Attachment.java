package de.itemis.graphing.model;

import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.model.style.VertexStyle;

public class Attachment extends GraphElement implements ISized
{
    public enum EHAlignment { Left, Center, Right }
    public enum EVAlignment { Top, Middle, Bottom }

    private final Vertex _parent;
    private final Size _size;
    private final int _rowIndex;
    private final int _colIndex;
    private final int _colSpan;
    private final int _rowSpan;
    private final EHAlignment _hAlign;
    private final EVAlignment _vAlign;
    private final boolean _affectDynamicLayout;

    private boolean _delegateInteractionToParent = false;

    public Attachment(Vertex vertex, String id, Size innerSize, int rowIndex, int colIndex, int colSpan, int rowSpan, EHAlignment hAlign, EVAlignment vAlign, boolean affectDynamicLayout)
    {
        super(vertex.getGraph(), id);
        _parent = vertex;
        _size = innerSize;
        _rowIndex = rowIndex;
        _colIndex = colIndex;
        _colSpan = colSpan;
        _rowSpan = rowSpan;
        _hAlign = hAlign;
        _vAlign = vAlign;
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

    public int getRowIndex()
    {
        return _rowIndex;
    }

    public int getColIndex()
    {
        return _colIndex;
    }

    public int getColSpan()
    {
        return _colSpan;
    }

    public int getRowSpan()
    {
        return _rowSpan;
    }

    public EHAlignment getHAlignment()
    {
        return _hAlign;
    }

    public EVAlignment getVAlignment()
    {
        return _vAlign;
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
