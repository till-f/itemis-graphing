package de.itemis.graphing.model;

import de.itemis.graphing.model.style.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Graph
{
    public static final String DEFAULT_SELECTION_COLOR = "0431B4";
    public static final String DEFAULT_CLICK_COLOR = "A0A0FF";

    private final HashMap<String, Edge> _uninsertedEdges = new HashMap<String, Edge>();

    private final HashMap<String, Vertex> _vertexes = new HashMap<String, Vertex>();
    private final HashMap<String, Edge> _edges = new HashMap<String, Edge>();
    private final HashMap<String, Attachment> _attachments = new HashMap<String, Attachment>();

    private BlockStyle[] _defaultVertexStyle = new BlockStyle[3];
    private EdgeStyle[] _defaultEdgeStyle = new EdgeStyle[3];
    private BlockStyle[] _defaultAttachmentStyle = new BlockStyle[3];

    private final HashSet<IGraphListener> _graphListeners = new HashSet<IGraphListener>();

    // -----------------------------------------------------------------------------------------------------------------
    // constructor

    public Graph()
    {
        _defaultVertexStyle[IStyled.EStyle.Regular.ordinal()] = new BlockStyle();
        _defaultEdgeStyle[IStyled.EStyle.Regular.ordinal()] = new EdgeStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Regular.ordinal()] = new BlockStyle();

        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()] = new BlockStyle();
        _defaultVertexStyle[IStyled.EStyle.Clicked.ordinal()].setFillColor(DEFAULT_CLICK_COLOR);
        _defaultEdgeStyle[IStyled.EStyle.Clicked.ordinal()] = new EdgeStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()] = new BlockStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Clicked.ordinal()].setFillColor(DEFAULT_CLICK_COLOR);

        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()] = new BlockStyle();
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()].setLineThickness(3.0);
        _defaultVertexStyle[IStyled.EStyle.Selected.ordinal()].setLineColor(DEFAULT_SELECTION_COLOR);
        _defaultEdgeStyle[IStyled.EStyle.Selected.ordinal()] = new EdgeStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()] = new BlockStyle();
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()].setLineThickness(3.0);
        _defaultAttachmentStyle[IStyled.EStyle.Selected.ordinal()].setLineColor(DEFAULT_SELECTION_COLOR);
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

    public Vertex addVertex(String id, double width, double height)
    {
        Vertex vertex = new Vertex(this, id, width, height);
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
        while (_edges.containsKey(edgeId))
        {
            edgeId = edgeId + "+";
        }

        return addEdge(edgeId, fromId, toId);
    }

    public Edge addEdge(String edgeId, String fromId, String toId)
    {
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

    public BaseGraphElement getElement(String id)
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

    public BlockStyle getDefaultVertexStyle(IStyled.EStyle styleSelector)
    {
        return _defaultVertexStyle[styleSelector.ordinal()];
    }

    public EdgeStyle getDefaultEdgeStyle(IStyled.EStyle styleSelector)
    {
        return _defaultEdgeStyle[styleSelector.ordinal()];
    }

    public BlockStyle getDefaultAttachmentStyle(IStyled.EStyle styleSelector)
    {
        return _defaultAttachmentStyle[styleSelector.ordinal()];
    }

    public void setDefaultVertexStyle(IStyled.EStyle styleSelector, BlockStyle newStyle)
    {
        _defaultVertexStyle[styleSelector.ordinal()] = newStyle;
    }

    public void setDefaultEdgeStyle(IStyled.EStyle styleSelector, EdgeStyle newStyle)
    {
        _defaultEdgeStyle[styleSelector.ordinal()] = newStyle;
    }

    public void setDefaultAttachmentStyle(IStyled.EStyle styleSelector, BlockStyle newStyle)
    {
        _defaultAttachmentStyle[styleSelector.ordinal()] = newStyle;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // internal methods

    public void styleChanged(BaseGraphElement element)
    {
        for(IGraphListener listener : _graphListeners)
        {
            listener.styleChanged(element);
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
