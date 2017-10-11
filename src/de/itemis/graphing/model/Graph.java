package de.itemis.graphing.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class Graph
{
    private final LinkedHashMap<String, Edge> _uninsertedEdges = new LinkedHashMap<String, Edge>();

    private final LinkedHashMap<String, Vertex> _vertexes = new LinkedHashMap<String, Vertex>();
    private final LinkedHashMap<String, Edge> _edges = new LinkedHashMap<String, Edge>();
    private final LinkedHashMap<String, AttachmentBase> _attachments = new LinkedHashMap<String, AttachmentBase>();

    private final LinkedHashSet<IGraphListener> _graphListeners = new LinkedHashSet<IGraphListener>();

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

    public List<AttachmentBase> getAttachments()
    {
        List<AttachmentBase> attachments = new LinkedList<AttachmentBase>();
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

    public void clearHighlighting()
    {
        for(Vertex v : _vertexes.values())
        {
            v.clearHighlighting();
        }
        for(Edge e : _edges.values())
        {
            e.clearHighlighting();
        }
        for(AttachmentBase a : _attachments.values())
        {
            a.clearHighlighting();
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
                for (Edge edge : vertex.getOutgoingEdges())
                {
                    if (edge.getToId().equals(id))
                        removeEdge(edge.getId());
                }
            }
            for(Vertex vertex : removedVertex.getTargets())
            {
                for (Edge edge : vertex.getIncomingEdges())
                {
                    if (edge.getFromId().equals(id))
                        removeEdge(edge.getId());
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
            _edges.remove(edgeId);

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

    public void labelPriorityChanged(GraphElement element)
    {
        for(IGraphListener listener : _graphListeners)
        {
            listener.labelPriorityChanged(element);
        }
    }

    public void attachmentAdded(AttachmentBase attachment)
    {
        _attachments.put(attachment.getId(), attachment);

        for(IGraphListener listener : _graphListeners)
        {
            listener.attachmentAdded(attachment);
        }
    }

    public void attachmentRemoved(AttachmentBase attachment)
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
