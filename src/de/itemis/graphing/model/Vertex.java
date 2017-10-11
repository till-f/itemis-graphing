package de.itemis.graphing.model;

import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.model.style.VertexStyle;

import java.util.*;

public class Vertex extends GraphElement implements ISized
{
    private final LinkedHashMap<String, AttachmentBase> _attachments = new LinkedHashMap<String, AttachmentBase>();
    private final HashMap<Integer, Double> _rowHeights = new HashMap<>();
    private final HashMap<Integer, Double> _colWidths = new HashMap<>();

    private LinkedHashSet<Edge> _outgoingEdges = new LinkedHashSet<Edge>();
    private LinkedHashSet<Edge> _incomingEdges = new LinkedHashSet<Edge>();

    private final Size _minimalSize;
    private final Padding _padding;
    private final double _cellSpacing;

    private boolean _isTableSizeDirty = true;

    private Double _x = null;
    private Double _y = null;

    public Vertex(Graph g, String id, Size minimalSize, double cellSpacing, Padding padding)
    {
        super(g, id);
        _minimalSize = minimalSize;
        _cellSpacing = cellSpacing;
        _padding = padding;
    }

    @Override
    protected void setInitialStyles()
    {
        _styleRegular = new VertexStyle();
        _styleClicked = new VertexStyle();
        _styleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleClicked.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleClicked.setIsInLevelForeground(true);
        _styleSelected = new VertexStyle();
        _styleSelected.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
        _styleSelected.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleSelected.setIsInLevelForeground(true);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // edges and relations to other vertexes

    public void addOutgoingEdge(Edge e)
    {
        _outgoingEdges.add(e);
    }

    public void removeOutgoingEdge(Edge e)
    {
        _outgoingEdges.remove(e);
    }

    public void addIncomingEdge(Edge e)
    {
        _incomingEdges.add(e);
    }

    public void removeIncomingEdge(Edge e)
    {
        _incomingEdges.remove(e);
    }

    public Set<Edge> getOutgoingEdges()
    {
        return new LinkedHashSet<>(_outgoingEdges);
    }

    public Set<Edge> getIncomingEdges()
    {
        return new LinkedHashSet<>(_incomingEdges);
    }

    public List<Vertex> getTargets()
    {
        LinkedList<Vertex> targets = new LinkedList<Vertex>();
        for (Edge e : _outgoingEdges)
        {
            targets.add(e.getTo());
        }
        return targets;
    }

    public List<Vertex> getSources()
    {
        LinkedList<Vertex> sources = new LinkedList<Vertex>();
        for (Edge e : _incomingEdges)
        {
            sources.add(e.getFrom());
        }
        return sources;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // attachments and size calculation

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, 1, 1, Attachment.EHAlignment.Center, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, int colSpan, int rowSpan)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, colSpan, rowSpan, Attachment.EHAlignment.Center, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EHAlignment hAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, 1, 1, hAlign, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, int colSpan, int rowSpan, Attachment.EHAlignment hAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, colSpan, rowSpan, hAlign, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, 1, 1, Attachment.EHAlignment.Center, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, int colSpan, int rowSpan, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, colSpan, rowSpan, Attachment.EHAlignment.Center, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EHAlignment hAlign, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, 1, 1, hAlign, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, int colSpan, int rowSpan, Attachment.EHAlignment hAlign, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, colSpan, rowSpan, hAlign, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, int colSpan, int rowSpan, Attachment.EHAlignment hAlign, Attachment.EVAlignment vAlign, boolean affectDynamicLayout)
    {
        Attachment a = new Attachment(this, id, new Size(width, height), rowIndex, colIndex, colSpan, rowSpan, hAlign, vAlign, affectDynamicLayout);

        clearTableSize();

        _attachments.put(id, a);
        _graph.attachmentAdded(a);

        styleChanged();

        return a;
    }

    public FloatingAttachment addFloatingAttachment(String id, double width, double height, double posAngle, double posDistance)
    {
        FloatingAttachment a = new FloatingAttachment(this, id, new Size(width, height), posAngle, posDistance);

        _attachments.put(id, a);
        _graph.attachmentAdded(a);

        return a;
    }

    public void removeAttachment(String id)
    {
        AttachmentBase a = _attachments.get(id);

        if (a != null)
        {
            clearTableSize();

            _attachments.remove(id);
            _graph.attachmentRemoved(a);

            styleChanged();
        }
    }

    public List<AttachmentBase> getAttachments()
    {
        LinkedList<AttachmentBase> attachments = new LinkedList<>();
        for (AttachmentBase a : _attachments.values())
        {
            attachments.add(a);
        }
        return attachments;
    }

    @Override
    public Size getSize()
    {
        if(_isTableSizeDirty) updateTableSize();

        return Size.max(_minimalSize, getAttachmentsSize().addPadding(_padding));
    }

    public Size getCellSize(int rowIndex, int colIndex, int colSpan, int rowSpan)
    {
        if(_isTableSizeDirty) updateTableSize();

        double width=0;
        double height=0;
        for (int idx=colIndex; idx<colIndex+colSpan; idx++)
        {
            width += getSizeFromMap(_colWidths, idx);
            if (idx>colIndex) width+= _cellSpacing;
        }
        for (int idx=rowIndex; idx<rowIndex+rowSpan; idx++)
        {
            height += getSizeFromMap(_rowHeights, idx);
            if (idx>rowIndex) height+= _cellSpacing;
        }
        return new Size(width, height);
    }

    public Size getCellOffset(int rowIndex, int colIndex)
    {
        if(_isTableSizeDirty) updateTableSize();

        double rowOffset = 0.5 * (_padding.getNorth() - _padding.getSouth());
        double colOffset = 0.5 * (_padding.getWest() -_padding.getEast());
        for (int idx=0; idx < ((rowIndex > colIndex) ? rowIndex : colIndex); idx++)
        {
            if (idx < rowIndex)
                rowOffset += getSizeFromMap(_rowHeights, idx) + _cellSpacing;
            if (idx < colIndex)
                colOffset += getSizeFromMap(_colWidths, idx) + _cellSpacing;
        }

        return new Size(colOffset, rowOffset);
    }

    public Size getAttachmentsSize()
    {
        if(_isTableSizeDirty) updateTableSize();

        double width = 0.0;
        for(Double colWidth : _colWidths.values())
        {
            width += colWidth + _cellSpacing;
        }
        double height = 0.0;
        for(Double rowHeihgt : _rowHeights.values())
        {
            height += rowHeihgt + _cellSpacing;
        }

        return new Size(width - _cellSpacing, height - _cellSpacing);
    }

    private void clearTableSize()
    {
        if (_isTableSizeDirty)
            return;

        _isTableSizeDirty = true;
        _rowHeights.clear();
        _colWidths.clear();
    }

    private void updateTableSize()
    {
        for (AttachmentBase a_base : _attachments.values())
        {
            if (!(a_base instanceof Attachment))
                continue;
            Attachment a = (Attachment)a_base;

            if (a.getColSpan() == 1)
            {
                updateTableSize_step(_colWidths, a.getColIndex(), a.getColSpan(), a.getSize().getWidth());
            }
            if (a.getRowSpan() == 1)
            {
                updateTableSize_step(_rowHeights, a.getRowIndex(), a.getRowSpan(), a.getSize().getHeight());
            }
        }
        for (AttachmentBase a_base : _attachments.values())
        {
            if (!(a_base instanceof Attachment))
                continue;
            Attachment a = (Attachment)a_base;

            if (a.getColSpan() > 1)
            {
                updateTableSize_step(_colWidths, a.getColIndex(), a.getColSpan(), a.getSize().getWidth());
            }
            if (a.getRowSpan() > 1)
            {
                updateTableSize_step(_rowHeights, a.getRowIndex(), a.getRowSpan(), a.getSize().getHeight());
            }
        }

        _isTableSizeDirty = false;
    }

    private void updateTableSize_step(HashMap<Integer, Double> sizes, int idx, int span, double size)
    {
        if (span < 1)
        {
            throw new RuntimeException("colSpan and rowSpan must be greater than 0");
        }
        else if (span == 1)
        {
            double maxSize = getSizeFromMap(sizes, idx);
            sizes.put(idx, Math.max(maxSize, size));
        }
        else
        {
            double existingSize = 0;
            double existingSizeWithoutSpacing = 0;
            for (int subIdx = idx; subIdx < idx + span; subIdx++)
            {
                double subSize = getSizeFromMap(sizes, subIdx);
                existingSizeWithoutSpacing += subSize;
                existingSize += subSize + _cellSpacing;
            }
            existingSize -= _cellSpacing;

            double extraNeeded = size - existingSize;
            if (extraNeeded <= 0)
                return;

            for (int subIdx = idx; subIdx < idx + span; subIdx++)
            {
                double subSize = getSizeFromMap(sizes, subIdx);
                double subExtra;                                    // proportional extra size needed for the column/row
                if (existingSizeWithoutSpacing == 0)
                    subExtra = (1.0 / span) * extraNeeded;          // special case if current row/column has zero size. in this case proportion would be infinite, thus we use proportion-of-span
                else
                    subExtra = (subSize / existingSizeWithoutSpacing) * extraNeeded;

                sizes.put(subIdx, subSize + subExtra);
            }
        }
    }

    private double getSizeFromMap(HashMap<Integer, Double> map, int idx)
    {
        Double value = map.get(idx);
        return value != null ? value : 0.0;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // placement

    public void reset()
    {
        _x = null;
        _y = null;
    }

    public void place(double x, double y, boolean force)
    {
        if (!force && _x != null && _y != null)
        {
            return;
        }

        _x = x;
        _y = y;
    }

    public Coordinates getCoordinates()
    {
        Double x = _x;
        Double y = _y;
        if (x == null || y == null) return null;

        return new Coordinates(x, y);
    }
}
