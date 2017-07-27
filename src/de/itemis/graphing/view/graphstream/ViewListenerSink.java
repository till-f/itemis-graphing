package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.IViewListener;
import org.graphstream.graph.Graph;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.Sink;

public class ViewListenerSink implements AttributeSink
{
    private final Graph _gsGraph;
    private final IViewListener _viewListener;

    public ViewListenerSink(Graph gsGraph, IViewListener viewListener)
    {
        _gsGraph = gsGraph;
        _viewListener = viewListener;
    }

    @Override
    public void graphAttributeAdded(String sourceId, long timeId, String attribute, Object value) {
        System.out.println("DEBUG: " + sourceId + "." + attribute + ": " + value);
    }

    @Override
    public void graphAttributeChanged(String s, long l, String s1, Object o, Object o1) {

    }

    @Override
    public void graphAttributeRemoved(String s, long l, String s1) {

    }

    @Override
    public void nodeAttributeAdded(String sourceId, long timeId, String nodeId, String attribute, Object value) {
        System.out.println("DEBUG: " + nodeId + "." + attribute + ": " + value);
    }

    @Override
    public void nodeAttributeChanged(String s, long l, String s1, String s2, Object o, Object o1) {

    }

    @Override
    public void nodeAttributeRemoved(String s, long l, String s1, String s2) {

    }

    @Override
    public void edgeAttributeAdded(String s, long l, String s1, String s2, Object o) {

    }

    @Override
    public void edgeAttributeChanged(String s, long l, String s1, String s2, Object o, Object o1) {

    }

    @Override
    public void edgeAttributeRemoved(String s, long l, String s1, String s2) {

    }
}
