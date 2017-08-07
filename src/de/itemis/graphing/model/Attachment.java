package de.itemis.graphing.model;

public class Attachment extends GraphElement implements ISized
{
    public enum EHAlignment { Left, Center, Right }
    public enum EVAlignment { Top, Middle, Bottom }

    private final Vertex _parent;
    private final Size _size;
    private final int _rowIndex;
    private final int _colIndex;
    private final EHAlignment _hAlign;
    private final EVAlignment _vAlign;
    private final boolean _affectDynamicLayout;

    private boolean _delegateInteractionToParent = false;

    public Attachment(Vertex vertex, String id, Size innerSize, int rowIndex, int colIndex, EHAlignment hAlign, EVAlignment vAlign, boolean affectDynamicLayout)
    {
        super(vertex.getGraph(), id);
        _parent = vertex;
        _size = innerSize;
        _rowIndex = rowIndex;
        _colIndex = colIndex;
        _hAlign = hAlign;
        _vAlign = vAlign;
        _affectDynamicLayout = affectDynamicLayout;
        setSelectable(false);
        setStyle(EStyle.Regular, _graph.getDefaultAttachmentStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, _graph.getDefaultAttachmentStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, _graph.getDefaultAttachmentStyle(EStyle.Selected));
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
