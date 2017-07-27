package de.itemis.graphing.util;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.Edge;
import de.itemis.graphing.model.IViewListener;
import de.itemis.graphing.model.IStyled;
import de.itemis.graphing.model.Vertex;

import java.util.Set;

public class AutomarkViewListener implements IViewListener
{
    private Vertex _lastMarkedVertex = null;

    @Override
    public void elementClickStart(BaseGraphElement element)
    {
        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            for(Edge edge : _lastMarkedVertex.getIncomingEdges())
            {
                edge.getStyle().setLineThickness(6.0);
                edge.getStyle().setLineColor("088A29");
                edge.getStyle().setzIndex(2);
                edge.getFrom().getStyle().setLineThickness(3.0);
                edge.getFrom().getStyle().setLineColor("088A29");
            }
            for(Edge edge : _lastMarkedVertex.getOutgoingEdges())
            {
                edge.getStyle().setLineThickness(6.0);
                edge.getStyle().setLineColor("DF7401");
                edge.getStyle().setzIndex(2);
                edge.getTo().getStyle().setLineThickness(3.0);
                edge.getTo().getStyle().setLineColor("DF7401");
            }
        }
    }

    @Override
    public void elementClickEnd(BaseGraphElement element)
    {
        if (_lastMarkedVertex != null && !_lastMarkedVertex.isSelectable())
        {
            restoreStyle(_lastMarkedVertex);
        }
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
        if (_lastMarkedVertex != null && unselected.contains(_lastMarkedVertex))
        {
            restoreStyle(_lastMarkedVertex);
        }
    }

    private void restoreStyle(Vertex vertex)
    {
        for(Edge edge : vertex.getIncomingEdges())
        {
            edge.getStyle(IStyled.EStyle.Regular).setLineThickness(1.0);
            edge.getStyle(IStyled.EStyle.Regular).setLineColor("000000");
            edge.getStyle(IStyled.EStyle.Regular).setzIndex(1);
            edge.getFrom().getStyle().setLineThickness(1.0);
            edge.getFrom().getStyle().setLineColor("000000");
        }
        for(Edge edge : vertex.getOutgoingEdges())
        {
            edge.getStyle(IStyled.EStyle.Regular).setLineThickness(1.0);
            edge.getStyle(IStyled.EStyle.Regular).setLineColor("000000");
            edge.getStyle(IStyled.EStyle.Regular).setzIndex(1);
            edge.getTo().getStyle().setLineThickness(1.0);
            edge.getTo().getStyle().setLineColor("000000");
        }
    }
}
