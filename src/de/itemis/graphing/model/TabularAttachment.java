package de.itemis.graphing.model;

import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.Style;

public class TabularAttachment extends AttachmentBase
{
    public enum EHAlignment { Left, Center, Right }
    public enum EVAlignment { Top, Middle, Bottom }

    private final int _rowIndex;
    private final int _colIndex;
    private final int _colSpan;
    private final int _rowSpan;
    private final EHAlignment _hAlign;
    private final EVAlignment _vAlign;

    public TabularAttachment(Vertex vertex, String id, Size size, int rowIndex, int colIndex, int colSpan, int rowSpan, EHAlignment hAlign, EVAlignment vAlign, boolean affectDynamicLayout)
    {
        super(vertex, id, size, affectDynamicLayout);

        _rowIndex = rowIndex;
        _colIndex = colIndex;
        _colSpan = colSpan;
        _rowSpan = rowSpan;
        _hAlign = hAlign;
        _vAlign = vAlign;
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
}
