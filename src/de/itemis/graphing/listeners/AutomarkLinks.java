package de.itemis.graphing.listeners;

import de.itemis.graphing.model.*;

import java.util.Set;

public class AutomarkLinks implements IInteractionListener
{
    private Vertex _lastMarkedVertex = null;
    private StyleStorage _styleStorage = new StyleStorage();

    @Override
    public void clickBegin(BaseGraphElement element)
    {
        if (_lastMarkedVertex != null)
        {
            selectStyle(_lastMarkedVertex, false);
            _lastMarkedVertex = null;
        }
    }

    @Override
    public void clickEnd(BaseGraphElement element)
    {
        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            selectStyle(_lastMarkedVertex, true);
        }
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
    }

    private void selectStyle(Vertex vertex, boolean isCustomStyleActive)
    {
        switchStyle(vertex, isCustomStyleActive);

        for(Edge edge : vertex.getIncomingEdges())
        {
            switchStyle(edge, isCustomStyleActive);
            switchStyle(edge.getFrom(), isCustomStyleActive);
        }

        for(Edge edge : vertex.getOutgoingEdges())
        {
            switchStyle(edge, isCustomStyleActive);
            switchStyle(edge.getTo(), isCustomStyleActive);
        }
    }

    private void switchStyle(BaseGraphElement e, boolean isCustomStyleActive)
    {
        if (isCustomStyleActive)
        {
            _styleStorage.storeStyle(e);
            e.getStyle().setLineThickness(Graph.DEFAULT_HL_LINE_THICKNESS);
            e.getStyle().setLineColor("FF0000");
        }
        else
        {
            _styleStorage.restoreStyle(e);
        }
    }

}
