package de.itemis.graphing.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Graph
{
    private final HashMap<String, Edge> _uninsertedEdges = new HashMap<String, Edge>();

    private final HashMap<String, Vertex> _vertexes = new HashMap<String, Vertex>();
    private final HashMap<String, Edge> _edges = new HashMap<String, Edge>();

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
        Vertex n = new Vertex(this, id, width, height);
        _vertexes.put(id, n);

        retryAddUninsertedEdges();

        return n;
    }

    public void removeVertex(String id)
    {
        Vertex removedVertex = _vertexes.get(id);
        if (removedVertex != null)
        {
            for(Vertex n : removedVertex.getSources())
            {
                for (Edge e : n.removeEdgesTo(removedVertex))
                {
                    _edges.remove(e);
                }
            }
            for(Vertex n : removedVertex.getTargets())
            {
                for (Edge e : n.removeEdgesFrom(removedVertex))
                {
                    _edges.remove(e);
                }
            }
        }
        _vertexes.remove(id);
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
    }
}
