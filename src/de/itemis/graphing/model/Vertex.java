package de.itemis.graphing.model;

import java.util.*;

public class Vertex extends GraphElement implements ISized
{
    private final LinkedHashMap<String, Attachment> _attachments = new LinkedHashMap<String, Attachment>();
    private final HashMap<Integer, Double> _rowHeights = new HashMap<>();
    private final HashMap<Integer, Double> _colWidths = new HashMap<>();

    private LinkedHashSet<Edge> _outgoingEdges = new LinkedHashSet<Edge>();
    private LinkedHashSet<Edge> _incomingEdges = new LinkedHashSet<Edge>();

    private Size _minimalSize;
    private double _cellSpacing = 0.03;

    private Double _x = null;
    private Double _y = null;

    public Vertex(Graph g, String id, Size minimalSize)
    {
        super(g, id);
        _minimalSize = minimalSize;
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
         return addAttachment(id, width, height, rowIndex, colIndex, Attachment.EHAlignment.Center, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EHAlignment hAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, hAlign, Attachment.EVAlignment.Middle, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, Attachment.EHAlignment.Center, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EHAlignment hAlign, Attachment.EVAlignment vAlign)
    {
        return addAttachment(id, width, height, rowIndex, colIndex, hAlign, vAlign, false);
    }

    public Attachment addAttachment(String id, double width, double height, int rowIndex, int colIndex, Attachment.EHAlignment hAlign, Attachment.EVAlignment vAlign, boolean affectDynamicLayout)
    {
        Attachment a = new Attachment(this, id, new Size(width, height), rowIndex, colIndex, hAlign, vAlign, affectDynamicLayout);

        _attachments.put(id, a);

        updateTableSize_attachmentAdded(rowIndex, colIndex, width, height);
        _graph.attachmentAdded(a);

        styleChanged();

        return a;
    }

    public void removeAttachment(String id)
    {
        Attachment a = _attachments.get(id);

        if (a != null)
        {
            _attachments.remove(id);

            updateTableSize_attachmentRemoved(a.getRowIndex(), a.getColIndex());
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
        return Size.max(_minimalSize, getAttachmentsSize());
    }

    public Size getCellSize(int rowIndex, int colIndex)
    {
        return new Size(_colWidths.get(colIndex), _rowHeights.get(rowIndex));
    }

    public Size getCellOffset(int rowIndex, int colIndex)
    {
        double rowOffset = 0.0;
        double colOffset = 0.0;
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

    private void updateTableSize_attachmentRemoved(int rowIndex, int colIndex)
    {
        _rowHeights.remove(rowIndex);
        _colWidths.remove(colIndex);

        double maxColWidth = 0.0;
        double maxRowHeight = 0.0;
        for (Attachment a : _attachments.values())
        {
            if (a.getRowIndex() == rowIndex)
                maxRowHeight = Math.max(maxRowHeight, a.getSize().getHeight());
            if (a.getColIndex() == colIndex)
                maxColWidth = Math.max(maxColWidth, a.getSize().getWidth());
        }

        if (maxRowHeight > 0.0)
            _rowHeights.put(rowIndex, maxRowHeight);

        if (maxColWidth > 0.0)
            _colWidths.put(colIndex, maxColWidth);
    }

    private void updateTableSize_attachmentAdded(int rowIndex, int colIndex, double width, double height)
    {
        Double maxRowHeight = _rowHeights.get(rowIndex);
        if (maxRowHeight == null) maxRowHeight = 0.0;
        _rowHeights.put(rowIndex, Math.max(maxRowHeight, height));

        Double maxColWidth = _colWidths.get(colIndex);
        if (maxColWidth == null) maxColWidth = 0.0;
        _colWidths.put(colIndex, Math.max(maxColWidth, width));
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
