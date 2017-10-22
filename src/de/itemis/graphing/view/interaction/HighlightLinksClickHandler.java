package de.itemis.graphing.view.interaction;

import de.itemis.graphing.model.Edge;
import de.itemis.graphing.model.GraphElement;
import de.itemis.graphing.model.Vertex;
import de.itemis.graphing.model.style.Style;

public class HighlightLinksClickHandler implements IClickHandler
{
    private enum EDirection { both, incoming, outgoing }

    private Vertex _lastMarkedVertex = null;

    private final int DEPTH;

    public HighlightLinksClickHandler()
    {
        this(1);
    }

    public HighlightLinksClickHandler(int depth)
    {
        DEPTH = depth;
    }

    @Override
    public void clickBegin(GraphElement element, ClickParameters params)
    {
        if (params.getButton() != ClickParameters.EButton.LEFT)
            return;

        if (_lastMarkedVertex != null)
        {
            selectStyle(_lastMarkedVertex, false, DEPTH, EDirection.both);
            _lastMarkedVertex = null;
        }
    }

    @Override
    public void clickEnd(GraphElement element, ClickParameters params)
    {
        if (params.getButton() != ClickParameters.EButton.LEFT)
            return;

        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            selectStyle(_lastMarkedVertex, true, DEPTH, EDirection.both);
        }
    }

    private void selectStyle(Vertex vertex, boolean setHighlighted, int depth, EDirection direction)
    {
        switchStyle(vertex, setHighlighted);

        if (depth==0)
            return;

        if (direction == EDirection.both || direction == EDirection.incoming)
        {
            for(Edge edge : vertex.getIncomingEdges())
            {
                switchStyle(edge, setHighlighted);
                selectStyle(edge.getFrom(), setHighlighted, depth-1, EDirection.incoming);
            }
        }

        if (direction == EDirection.both || direction == EDirection.outgoing)
        {
            for (Edge edge : vertex.getOutgoingEdges()) {
                switchStyle(edge, setHighlighted);
                selectStyle(edge.getTo(), setHighlighted, depth - 1, EDirection.outgoing);
            }
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
