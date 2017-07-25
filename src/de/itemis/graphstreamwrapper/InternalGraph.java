package de.itemis.graphstreamwrapper;

import scala.collection.mutable.LinkedListLike;

import java.util.*;

public class InternalGraph
{
    private final LinkedList<Object[]> _uninsertedEdges = new LinkedList<Object[]>();

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

    public InternalNode addNode(String id, double width, double height, Object userObject)
    {
        InternalNode n = new InternalNode(id, width, height, userObject);
        _nodes.put(id, n);
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

    public void addEdge(String edgeId, String fromId, String toId, Object userObject)
    {
        InternalEdge e = createEdge(edgeId, fromId, toId, userObject);

        if (e == null)
        {
            Object[] ue = new Object[4];
            ue[0] = edgeId;
            ue[1] = fromId;
            ue[2] = toId;
            ue[3] = userObject;
            _uninsertedEdges.add(ue);
        }
        else
        {
            _edges.put(edgeId, e);
            e.getFrom().addOutgoingEdge(e);
            e.getTo().addIncomingEdge(e);
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
    }

    private InternalEdge createEdge(String edgeId, String fromId, String toId, Object userObject)
    {
        InternalNode from = _nodes.get(fromId);
        InternalNode to = _nodes.get(toId);

        if (from == null || to == null)
            return null;

        return new InternalEdge(edgeId, from, to, userObject);
    }
}
