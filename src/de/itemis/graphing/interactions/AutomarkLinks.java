package de.itemis.graphing.interactions;

import de.itemis.graphing.model.Edge;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.Vertex;
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

    private void selectStyle(Vertex vertex, boolean setHighlighted)
    {
        switchStyle(vertex, setHighlighted);

        for(Edge edge : vertex.getIncomingEdges())
        {
            switchStyle(edge, setHighlighted);
            switchStyle(edge.getFrom(), setHighlighted);
        }

        for(Edge edge : vertex.getOutgoingEdges())
        {
            switchStyle(edge, setHighlighted);
            switchStyle(edge.getTo(), setHighlighted);
        }
    }

    private void switchStyle(GraphElement e, boolean setHighlighted)
    {
        if (setHighlighted)
        {
            Style s = e.getStyleRegular();
            s.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HL);
            s.setLineColor("FF0000");
            s.setIsInLevelForeground(true);
            e.pushHighlighting(s);
        }
        else
        {
            e.popHighlighting();
        }
    }
}
