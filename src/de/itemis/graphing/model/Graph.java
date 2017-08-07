package de.itemis.graphing.model;

import de.itemis.graphing.model.style.AttachmentStyle;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.VertexStyle;

import java.util.*;

public class Graph
{
    public static final String DEFAULT_HL_LINE_COLOR = "0099FF";
    public static final Double DEFAULT_HL_LINE_THICKNESS = 3.0;
    public static final Double DEFAULT_HL_LINE_THICKNESS_EDGE = 5.0;

    private final LinkedHashMap<String, Edge> _uninsertedEdges = new LinkedHashMap<String, Edge>();

    private final LinkedHashMap<String, Vertex> _vertexes = new LinkedHashMap<String, Vertex>();
    private final LinkedHashMap<String, Edge> _edges = new LinkedHashMap<String, Edge>();
    private final LinkedHashMap<String, Attachment> _attachments = new LinkedHashMap<String, Attachment>();

    private VertexStyle[] _defaultVertexStyle = new VertexStyle[IStyled.EStyle.values().length];
    private EdgeStyle[] _defaultEdgeStyle = new EdgeStyle[IStyled.EStyle.values().length];
    private AttachmentStyle[] _defaultAttachmentStyle = new AttachmentStyle[IStyled.EStyle.values().length];

    private final LinkedHashSet<IGraphListener> _graphListeners = new LinkedHashSet<IGraphListener>();

    // -----------------------------------------------------------------------------------------------------------------
    // constructor

    public Graph()
    {
        _defaultVertexStyle[IStyled.EStyle.Regular.ordinal()] = new VertexStyle();
        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()] = new VertexStyle();
        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()].setzIndex(4);
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()] = new VertexStyle();
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()].setzIndex(4);
        _defaultVertexStyle[IStyled.EStyle.Highlighted.ordinal()] = new VertexStyle();
        _defaultVertexStyle[IStyled.EStyle.Highlighted.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultVertexStyle[IStyled.EStyle.Highlighted.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultVertexStyle[IStyled.EStyle.Highlighted.ordinal()].setzIndex(4);

        _defaultEdgeStyle[IStyled.EStyle.Regular.ordinal()] = new EdgeStyle();
        _defaultEdgeStyle[IStyled.EStyle.Clicked.ordinal()] = new EdgeStyle();  // edges cannot b clicked...
        _defaultEdgeStyle[IStyled.EStyle.Clicked.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS_EDGE);
        _defaultEdgeStyle[IStyled.EStyle.Clicked.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultEdgeStyle[IStyled.EStyle.Clicked.ordinal()].setzIndex(2);
        _defaultEdgeStyle[IStyled.EStyle.Selected.ordinal()] = new EdgeStyle();
        _defaultEdgeStyle[IStyled.EStyle.Selected.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS_EDGE);
        _defaultEdgeStyle[IStyled.EStyle.Selected.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultEdgeStyle[IStyled.EStyle.Selected.ordinal()].setzIndex(2);
        _defaultEdgeStyle[IStyled.EStyle.Highlighted.ordinal()] = new EdgeStyle();
        _defaultEdgeStyle[IStyled.EStyle.Highlighted.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS_EDGE);
        _defaultEdgeStyle[IStyled.EStyle.Highlighted.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultEdgeStyle[IStyled.EStyle.Highlighted.ordinal()].setzIndex(2);

        _defaultAttachmentStyle[IStyled.EStyle.Regular.ordinal()] = new AttachmentStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()] = new AttachmentStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()].setzIndex(6);
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()] = new AttachmentStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()].setzIndex(6);
        _defaultAttachmentStyle[IStyled.EStyle.Highlighted.ordinal()] = new AttachmentStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Highlighted.ordinal()].setLineThickness(DEFAULT_HL_LINE_THICKNESS);
        _defaultAttachmentStyle[IStyled.EStyle.Highlighted.ordinal()].setLineColor(DEFAULT_HL_LINE_COLOR);
        _defaultAttachmentStyle[IStyled.EStyle.Highlighted.ordinal()].setzIndex(6);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // base graph elements

    public List<Vertex> getVertexes()
    {
        List<Vertex> vertexes = new LinkedList<Vertex>();
        vertexes.addAll(_vertexes.values());
        return vertexes;
    }

    public List<Edge> getEdges()
    {
        List<Edge> edges = new LinkedList<Edge>();
        edges.addAll(_edges.values());
        return edges;
    }

    public List<Attachment> getAttachments()
    {
        List<Attachment> attachments = new LinkedList<Attachment>();
        attachments.addAll(_attachments.values());
        return attachments;
    }

    public void clear()
    {
        _vertexes.clear();
        _edges.clear();
        _attachments.clear();
        _uninsertedEdges.clear();

        for(IGraphListener listener : _graphListeners)
        {
            listener.graphCleared();
        }
    }

    public void resetHighlighting()
    {
        for(Vertex v : _vertexes.values())
        {
            v.endHighlight();
        }
        for(Edge e : _edges.values())
        {
            e.endHighlight();
        }
        for(Attachment a : _attachments.values())
        {
            a.endHighlight();
        }
    }

    public Vertex addVertex(String id, double width, double height)
    {
        return addVertex(id, width, height, 0.0, new Padding(0.0));
    }

    public Vertex addVertex(String id, double width, double height, double cellSpacing)
    {
        return addVertex(id, width, height, cellSpacing, new Padding(0.0));
    }

    public Vertex addVertex(String id, double width, double height, Padding padding)
    {
        return addVertex(id, width, height, 0.0, padding);
    }

    public Vertex addVertex(String id, double width, double height, double cellSpacing, Padding padding)
    {
        if (_vertexes.containsKey(id))
            return null;

        Vertex vertex = new Vertex(this, id, new Size(width, height), cellSpacing, padding);
        _vertexes.put(id, vertex);

        for(IGraphListener listener : _graphListeners)
        {
            listener.vertexAdded(vertex);
        }

        retryAddUninsertedEdges();

        return vertex;
    }

    public void removeVertex(String id)
    {
        Vertex removedVertex = _vertexes.get(id);
        if (removedVertex != null)
        {
            for(Vertex vertex : removedVertex.getSources())
            {
                for (Edge edge : vertex.removeEdgesTo(removedVertex))
                {
                    _edges.remove(edge);
                }
            }
            for(Vertex vertex : removedVertex.getTargets())
            {
                for (Edge edge : vertex.removeEdgesFrom(removedVertex))
                {
                    _edges.remove(edge);
                }
            }

            _vertexes.remove(id);

            for(IGraphListener listener : _graphListeners)
            {
                listener.vertexRemoved(removedVertex);
            }
        }
    }

    public Edge addEdge(String fromId, String toId)
    {
        String edgeId = fromId + "." + toId;
        return addEdge(edgeId, fromId, toId);
    }

    public Edge addEdge(String edgeId, String fromId, String toId)
    {
        if (_edges.containsKey(edgeId) || _uninsertedEdges.containsKey(edgeId))
            return null;

        Vertex from = _vertexes.get(fromId);
        Vertex to = _vertexes.get(toId);

        if (from == null || to == null)
        {
            Edge ue = new Edge(this, edgeId, fromId, toId);
            _uninsertedEdges.put(edgeId, ue);
            return ue;
        }
        else
        {
            Edge e = new Edge(this, edgeId, from, to);
            doAddEdge(e);
            return e;
        }
    }

    public void removeEdge(String edgeId)
    {
        Edge e = _edges.get(edgeId);

        if (e != null)
        {
            e.getFrom().removeOutgoingEdge(e);
            e.getTo().removeIncomingEdge(e);
            _edges.remove(e);

            for(IGraphListener listener : _graphListeners)
            {
                listener.edgeRemoved(e);
            }
        }

        _uninsertedEdges.remove(edgeId);
    }

    public List<Vertex> getRootVertexes()
    {
        LinkedList<Vertex> rootVertexes = new LinkedList<Vertex>();
        for (Vertex vertex : _vertexes.values())
        {
            if (vertex.getSources().size() < 1)
            {
                rootVertexes.add(vertex);
            }
        }
        return rootVertexes;
    }

    public GraphElement getElement(String id)
    {
        if (_vertexes.containsKey(id))
            return _vertexes.get(id);
        if (_edges.containsKey(id))
            return _edges.get(id);
        if (_attachments.containsKey(id))
            return _attachments.get(id);

        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // listener registration

    public void registerGraphListener(IGraphListener listener)
    {
        _graphListeners.add(listener);
    }

    public void removeGraphListener(IGraphListener listener)
    {
        _graphListeners.remove(listener);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // default styles

    public VertexStyle getDefaultVertexStyle(IStyled.EStyle styleSelector)
    {
        return (VertexStyle)_defaultVertexStyle[styleSelector.ordinal()].getCopy();
    }

    public EdgeStyle getDefaultEdgeStyle(IStyled.EStyle styleSelector)
    {
        return (EdgeStyle) _defaultEdgeStyle[styleSelector.ordinal()].getCopy();
    }

    public AttachmentStyle getDefaultAttachmentStyle(IStyled.EStyle styleSelector)
    {
        return (AttachmentStyle) _defaultAttachmentStyle[styleSelector.ordinal()].getCopy();
    }

    public void setDefaultVertexStyle(IStyled.EStyle styleSelector, BlockStyle newStyle)
    {
        _defaultVertexStyle[styleSelector.ordinal()] = (VertexStyle)newStyle.getCopy();
    }

    public void setDefaultEdgeStyle(IStyled.EStyle styleSelector, EdgeStyle newStyle)
    {
        _defaultEdgeStyle[styleSelector.ordinal()] = (EdgeStyle)newStyle.getCopy();
    }

    public void setDefaultAttachmentStyle(IStyled.EStyle styleSelector, BlockStyle newStyle)
    {
        _defaultAttachmentStyle[styleSelector.ordinal()] = (AttachmentStyle)newStyle.getCopy();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // internal methods

    public void styleChanged(GraphElement element)
    {
        for(IGraphListener listener : _graphListeners)
        {
            listener.styleChanged(element);
        }
    }

    public void labelChanged(GraphElement element)
    {
        for(IGraphListener listener : _graphListeners)
        {
            listener.labelChanged(element);
        }
    }

    public void attachmentAdded(Attachment attachment)
    {
        _attachments.put(attachment.getId(), attachment);

        for(IGraphListener listener : _graphListeners)
        {
            listener.attachmentAdded(attachment);
        }
    }

    public void attachmentRemoved(Attachment attachment)
    {
        _attachments.remove(attachment.getId());

        for(IGraphListener listener : _graphListeners)
        {
            listener.attachmentRemoved(attachment);
        }
    }

    private void retryAddUninsertedEdges()
    {
        List<String> insertedEdgeIds = new LinkedList<>();
        for(Edge ue : _uninsertedEdges.values())
        {
            Vertex from = _vertexes.get(ue.getFromId());
            Vertex to = _vertexes.get(ue.getToId());
            if (from != null && to != null)
            {
                ue.setFromTo(from, to);
                doAddEdge(ue);
                insertedEdgeIds.add(ue.getId());
            }
        }

        for(String insertedId : insertedEdgeIds)
        {
            _uninsertedEdges.remove(insertedId);
        }
    }

    private void doAddEdge(Edge e)
    {
        _edges.put(e.getId(), e);
        e.getFrom().addOutgoingEdge(e);
        e.getTo().addIncomingEdge(e);

        for(IGraphListener listener : _graphListeners)
        {
            listener.edgeAdded(e);
        }
    }
}
