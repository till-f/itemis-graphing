package de.itemis.graphing.listeners;

import de.itemis.graphing.model.*;
import de.itemis.graphing.model.IInteractionListener;
import de.itemis.graphing.model.style.Style;

import java.util.HashMap;
import java.util.Set;

public class AutomarkLinks implements IInteractionListener
{
    private Vertex _lastMarkedVertex = null;

    private HashMap<BaseGraphElement, Style> _storedStyles = new HashMap<>();

    @Override
    public void elementClickStart(BaseGraphElement element)
    {
        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            highlight(_lastMarkedVertex, false);
        }
    }

    @Override
    public void elementClickEnd(BaseGraphElement element)
    {
        if (_lastMarkedVertex != null)
        {
            highlight(_lastMarkedVertex, true);
            _lastMarkedVertex = null;
        }
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
    }

    private void highlight(Vertex v, boolean isRestore)
    {
        for(Edge edge : v.getIncomingEdges())
        {
            updateStyle(edge, true, isRestore);
            updateStyle(edge.getFrom(), true, isRestore);
        }

        for(Edge edge : v.getOutgoingEdges())
        {
            updateStyle(edge, false, isRestore);
            updateStyle(edge.getTo(), false, isRestore);
        }

    }

    private void updateStyle(BaseGraphElement element, boolean isIncomingEdge, boolean isRestore)
    {
        if (isRestore)
        {
            if (!_storedStyles.containsKey(element))
                return;

            element.setStyle(_storedStyles.get(element));

            _storedStyles.remove(element);
        }
        else
        {
            if (_storedStyles.containsKey(element))
                return;

            _storedStyles.put(element, element.getStyle().getCopy());

            if (isIncomingEdge)
            {
                element.getStyle().setLineThickness(4.0);
                element.getStyle().setLineColor("088A29");
                if (element instanceof Edge)
                {
                    element.getStyle().setzIndex(1);
                }
            }
            else
            {
                element.getStyle().setLineThickness(4.0);
                element.getStyle().setLineColor("DF7401");
                if (element instanceof Edge)
                {
                    element.getStyle().setzIndex(1);
                }
            }
        }
    }
}
