package de.itemis.graphing.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graph<T>
{
    private final LinkedHashMap<String, Edge<T>> _uninsertedEdges = new LinkedHashMap<String, Edge<T>>();

    private final LinkedHashMap<String, Vertex<T>> _vertexes = new LinkedHashMap<String, Vertex<T>>();
    private final LinkedHashMap<String, Edge<T>> _edges = new LinkedHashMap<String, Edge<T>>();
    private final LinkedHashMap<String, AttachmentBase<T>> _attachments = new LinkedHashMap<String, AttachmentBase<T>>();

    private final LinkedHashSet<IGraphListener<T>> _graphListeners = new LinkedHashSet<IGraphListener<T>>();

    // -----------------------------------------------------------------------------------------------------------------
    // base graph elements

    public List<Vertex<T>> getVertexes()
    {
        List<Vertex<T>> vertexes = new LinkedList<Vertex<T>>();
        vertexes.addAll(_vertexes.values());
        return vertexes;
    }

    public List<Edge<T>> getEdges()
    {
        List<Edge<T>> edges = new LinkedList<Edge<T>>();
        edges.addAll(_edges.values());
        return edges;
    }

    public List<AttachmentBase<T>> getAttachments()
    {
        List<AttachmentBase<T>> attachments = new LinkedList<AttachmentBase<T>>();
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

    public Vertex<T> addVertex(String id, double width, double height)
    {
        return addVertex(id, width, height, 0.0, new Padding(0.0));
    }

    public Vertex<T> addVertex(String id, double width, double height, double cellSpacing)
    {
        return addVertex(id, width, height, cellSpacing, new Padding(0.0));
    }

    public Vertex<T> addVertex(String id, double width, double height, Padding padding)
    {
        return addVertex(id, width, height, 0.0, padding);
    }

    public Vertex<T> addVertex(String id, double width, double height, double cellSpacing, Padding padding)
    {
        if (_vertexes.containsKey(id))
            return null;

        Vertex<T> vertex = new Vertex<T>(this, id, new Size(width, height), cellSpacing, padding);
        _vertexes.put(id, vertex);

        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.vertexAdded(vertex);
        }

        retryAddUninsertedEdges();

        return vertex;
    }

    public void removeVertex(String id)
    {
        Vertex<T> removedVertex = _vertexes.get(id);
        if (removedVertex != null)
        {
            for(Vertex<T> vertex : removedVertex.getSources())
            {
                for (Edge<T> edge : vertex.getOutgoingEdges())
                {
                    if (edge.getToId().equals(id))
                        removeEdge(edge.getId());
                }
            }
            for(Vertex<T> vertex : removedVertex.getTargets())
            {
                for (Edge<T> edge : vertex.getIncomingEdges())
                {
                    if (edge.getFromId().equals(id))
                        removeEdge(edge.getId());
                }
            }

            _vertexes.remove(id);

            for(IGraphListener<T> listener : _graphListeners)
            {
                listener.vertexRemoved(removedVertex);
            }
        }
    }

    public Edge<T> addEdge(String fromId, String toId)
    {
        String edgeId = fromId + "." + toId;
        return addEdge(edgeId, fromId, toId);
    }

    public Edge<T> addEdge(String edgeId, String fromId, String toId)
    {
        if (_edges.containsKey(edgeId) || _uninsertedEdges.containsKey(edgeId))
            return null;

        Vertex<T> from = _vertexes.get(fromId);
        Vertex<T> to = _vertexes.get(toId);

        if (from == null || to == null)
        {
            Edge<T> ue = new Edge<T>(this, edgeId, fromId, toId);
            _uninsertedEdges.put(edgeId, ue);
            return ue;
        }
        else
        {
            Edge<T> e = new Edge<T>(this, edgeId, from, to);
            doAddEdge(e);
            return e;
        }
    }

    public void removeEdge(String edgeId)
    {
        Edge<T> e = _edges.get(edgeId);

        if (e != null)
        {
            e.getFrom().removeOutgoingEdge(e);
            e.getTo().removeIncomingEdge(e);
            _edges.remove(edgeId);

            for(IGraphListener<T> listener : _graphListeners)
            {
                listener.edgeRemoved(e);
            }
        }

        _uninsertedEdges.remove(edgeId);
    }

    public List<Edge<T>> getEdgesBetween(String vertexIdA, String vertexIdB)
    {
        Vertex<T> vertexA = _vertexes.get(vertexIdA);
        Vertex<T> vertexB = _vertexes.get(vertexIdB);
        if (vertexA != null && vertexB != null)
        {
            Stream<Edge<T>> e1 = vertexA.getOutgoingEdges().stream().filter(edge -> edge.getTo().equals(vertexB));
            Stream<Edge<T>> e2 = vertexB.getOutgoingEdges().stream().filter(edge -> edge.getTo().equals(vertexA));
            return Stream.concat(e1,  e2).collect(Collectors.toList());
        }
        else
        {
            return new LinkedList<Edge<T>>();
        }
    }

    public List<Vertex<T>> getRootVertexes()
    {
        return _vertexes.values().stream().filter(it -> it.getSources().size() < 1).collect(Collectors.toList());
    }

    public GraphElement<T> getElement(String id)
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

    public void registerGraphListener(IGraphListener<T> listener)
    {
        _graphListeners.add(listener);
    }

    public void removeGraphListener(IGraphListener<T> listener)
    {
        _graphListeners.remove(listener);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // internal methods

    public void styleChanged(GraphElement<T> element)
    {
        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.styleChanged(element);
        }
    }

    public void labelChanged(GraphElement<T> element)
    {
        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.labelChanged(element);
        }
    }

    public void labelPriorityChanged(GraphElement<T> element)
    {
        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.labelPriorityChanged(element);
        }
    }

    public void attachmentAdded(AttachmentBase<T> attachment)
    {
        String id = attachment.getId();
        if (_attachments.containsKey(id))
            throw new RuntimeException("attachment with Id '" + id + "' already inserted, attachment Ids must be globally unique (not only per vertex)");

        _attachments.put(id, attachment);

        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.attachmentAdded(attachment);
        }
    }

    public void attachmentRemoved(AttachmentBase<T> attachment)
    {
        _attachments.remove(attachment.getId());

        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.attachmentRemoved(attachment);
        }
    }

    private void retryAddUninsertedEdges()
    {
        List<String> insertedEdgeIds = new LinkedList<>();
        for(Edge<T> ue : _uninsertedEdges.values())
        {
            Vertex<T> from = _vertexes.get(ue.getFromId());
            Vertex<T> to = _vertexes.get(ue.getToId());
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

    private void doAddEdge(Edge<T> e)
    {
        _edges.put(e.getId(), e);
        e.getFrom().addOutgoingEdge(e);
        e.getTo().addIncomingEdge(e);

        for(IGraphListener<T> listener : _graphListeners)
        {
            listener.edgeAdded(e);
        }
    }
}
