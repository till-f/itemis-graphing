package de.itemis.graphstreamwrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InternalGraph
{
    private final HashMap<String, InternalEdge> _uninsertedEdges = new HashMap<String, InternalEdge>();

    private final HashMap<String, InternalNode> _nodes = new HashMap<String, InternalNode>();
    private final HashMap<String, InternalEdge> _edges = new HashMap<String, InternalEdge>();

    public List<InternalNode> getNodes()
    {
        List<InternalNode> nodes = new LinkedList<InternalNode>();
        nodes.addAll(_nodes.values());
        return nodes;
    }

    public List<InternalEdge> getEdges()
    {
        List<InternalEdge> edges = new LinkedList<InternalEdge>();
        edges.addAll(_edges.values());
        return edges;
    }

    public InternalNode addNode(String id, double width, double height)
    {
        InternalNode n = new InternalNode(id, width, height);
        _nodes.put(id, n);

        retryAddUninsertedEdges();

        return n;
    }

    public void removeNode(String id)
    {
        InternalNode removedNode = _nodes.get(id);
        if (removedNode != null)
        {
            for(InternalNode n : removedNode.getSources())
            {
                for (InternalEdge e : n.removeEdgesTo(removedNode))
                {
                    _edges.remove(e);
                }
            }
            for(InternalNode n : removedNode.getTargets())
            {
                for (InternalEdge e : n.removeEdgesFrom(removedNode))
                {
                    _edges.remove(e);
                }
            }
        }
        _nodes.remove(id);
    }

    public InternalEdge addEdge(String fromId, String toId)
    {
        String edgeId = fromId + "." + toId;
        while (_edges.containsKey(edgeId))
        {
            edgeId = edgeId + "+";
        }

        InternalNode from = _nodes.get(fromId);
        InternalNode to = _nodes.get(toId);

        if (from == null || to == null)
        {
            InternalEdge ue = new InternalEdge(edgeId, fromId, toId);
            _uninsertedEdges.put(edgeId, ue);
            return ue;
        }
        else
        {
            InternalEdge e = new InternalEdge(edgeId, from, to);
            doAddEdge(e);
            return e;
        }
    }

    public void removeEdge(String edgeId)
    {
        InternalEdge e = _edges.get(edgeId);

        if (e != null)
        {
            e.getFrom().removeOutgoingEdge(e);
            e.getTo().removeIncomingEdge(e);
            _edges.remove(e);
        }

        _uninsertedEdges.remove(edgeId);
    }

    private void retryAddUninsertedEdges()
    {
        List<String> insertedEdgeIds = new LinkedList<>();
        for(InternalEdge ue : _uninsertedEdges.values())
        {
            InternalNode from = _nodes.get(ue.getFromId());
            InternalNode to = _nodes.get(ue.getToId());
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

    private void doAddEdge(InternalEdge e)
    {
        _edges.put(e.getId(), e);
        e.getFrom().addOutgoingEdge(e);
        e.getTo().addIncomingEdge(e);
    }
}
