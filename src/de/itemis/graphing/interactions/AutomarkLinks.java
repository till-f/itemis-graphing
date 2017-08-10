package de.itemis.graphing.interactions;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.style.Style;
import de.itemis.graphing.view.IInteractionListener;

import java.util.Set;

public class AutomarkLinks implements IInteractionListener
{
    private Vertex _lastMarkedVertex = null;

    @Override
    public void clickBegin(GraphElement element, ClickParameters params)
    {
        if (_lastMarkedVertex != null)
        {
            selectStyle(_lastMarkedVertex, false);
            _lastMarkedVertex = null;
        }
    }

    @Override
    public void clickEnd(GraphElement element, ClickParameters params)
    {
        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            selectStyle(_lastMarkedVertex, true);
        }
    }

    @Override
    public void selectionChanged(Set<GraphElement> selected, Set<GraphElement> unselected)
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

    private void switchStyle(GraphElement e, boolean isCustomStyleActive)
    {
        if (isCustomStyleActive)
        {
            Style s = e.getStyleRegular();
            s.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
            s.setLineColor("FF0000");
            if (e instanceof Edge)
            {
                s.setIsInLevelForeground(true);
            }
            e.pushHighlighting(s);
        }
        else
        {
            e.popHighlighting();
        }
    }

}
