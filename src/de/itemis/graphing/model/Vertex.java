package de.itemis.graphing.model;

import java.util.*;

public class Vertex extends GraphElement implements ISized
{
    private final LinkedHashMap<String, Attachment> _attachments = new LinkedHashMap<String, Attachment>();
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
        setStyle(EStyle.Regular, g.getDefaultVertexStyle(EStyle.Regular));
        setStyle(EStyle.Clicked, g.getDefaultVertexStyle(EStyle.Clicked));
        setStyle(EStyle.Selected, g.getDefaultVertexStyle(EStyle.Selected));
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
        return _outgoingEdges;
    }

    public Set<Edge> getIncomingEdges()
    {
        return _incomingEdges;
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

    public List<Edge> removeEdgesTo(Vertex target)
    {
        LinkedList<Edge> toRemove = new LinkedList<Edge>();
        for (Edge e : _outgoingEdges)
        {
            if (e.getTo() == target)
                toRemove.add(e);
        }
        _outgoingEdges.removeAll(toRemove);

        return toRemove;
    }

    public List<Edge> removeEdgesFrom(Vertex source)
    {
        LinkedList<Edge> toRemove = new LinkedList<Edge>();
        for (Edge e : _incomingEdges)
        {
            if (e.getFrom() == source)
                toRemove.add(e);
        }
        _incomingEdges.removeAll(toRemove);

        return toRemove;
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

    public void removeAttachment(String id)
    {
        Attachment a = _attachments.get(id);

        if (a != null)
        {
            clearTableSize();

            _attachments.remove(id);
            _graph.attachmentRemoved(a);

            styleChanged();
        }
    }

    public List<Attachment> getAttachments()
    {
        LinkedList<Attachment> attachments = new LinkedList<>();
        for (Attachment a : _attachments.values())
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
            width += _colWidths.get(idx);
            if (idx>colIndex) width+= _cellSpacing;
        }
        for (int idx=rowIndex; idx<rowIndex+rowSpan; idx++)
        {
            height += _rowHeights.get(idx);
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
                rowOffset += (_rowHeights.get(idx) != null ? _rowHeights.get(idx) : 0.0) + _cellSpacing;
            if (idx < colIndex)
                colOffset += (_colWidths.get(idx) != null ? _colWidths.get(idx) : 0.0) + _cellSpacing;
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
        for (Attachment a : _attachments.values())
        {
            if (a.getColSpan() == 1)
            {
                updateTableSize_step(_colWidths, a.getColIndex(), a.getColSpan(), a.getSize().getWidth());
            }
            if (a.getRowSpan() == 1)
            {
                updateTableSize_step(_rowHeights, a.getRowIndex(), a.getRowSpan(), a.getSize().getHeight());
            }
        }
        for (Attachment a : _attachments.values())
        {
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
            Double maxSize = sizes.get(idx);
            if (maxSize == null) maxSize = 0.0;
            sizes.put(idx, Math.max(maxSize, size));
        }
        else
        {
            double existingSize = 0;
            double existingSizeWithoutSpacing = 0;
            for (int subIdx = idx; subIdx < idx + span; subIdx++)
            {
                double subSize = sizes.get(subIdx) != null ? sizes.get(subIdx) : 0.0;
                existingSizeWithoutSpacing += subSize;
                existingSize += subSize + _cellSpacing;
            }
            existingSize -= _cellSpacing;

            double extraNeeded = size - existingSize;
            if (extraNeeded <= 0)
                return;

            for (int subIdx = idx; subIdx < idx + span; subIdx++)
            {
                double subSize = sizes.get(subIdx) != null ? sizes.get(subIdx) : 0.0;
                double subExtra = subSize / existingSizeWithoutSpacing * extraNeeded;
                sizes.put(subIdx, subSize + subExtra);
            }
        }
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

    public boolean isPlaced()
    {
        return _x != null && _y != null;
    }

    public double getX()
    {
        return _x;
    }

    public double getY()
    {
        return _y;
    }

}
