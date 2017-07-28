package de.itemis.graphing.listeners;

import de.itemis.graphing.model.*;

import java.util.Set;

public class AutomarkLinks implements IInteractionListener
{
    private Vertex _lastMarkedVertex = null;
    private StyleStorage _styleStorage = new StyleStorage();

    @Override
    public void clickBegin()
    {
        if (_lastMarkedVertex != null)
        {
            selectStyle(_lastMarkedVertex, false);
            _lastMarkedVertex = null;
        }
    }

    @Override
    public void clickBegin(BaseGraphElement element)
    {
        if (element instanceof Vertex)
        {
            _lastMarkedVertex = (Vertex) element;
            selectStyle(_lastMarkedVertex, true);
        }
    }

    @Override
    public void clickEnd(BaseGraphElement element)
    {
    }

    @Override
    public void selectionChanged(Set<BaseGraphElement> selected, Set<BaseGraphElement> unselected)
    {
    }

    private void selectStyle(Vertex vertex, boolean select)
    {
        IStyled.EStyle styleType;
        if (select)
        {
            _styleStorage.storeStyle(vertex);
            vertex.getStyle().setLineThickness(6.0);
            styleType = IStyled.EStyle.Selected;
        }
        else
        {
            _styleStorage.restoreStyle(vertex);
            styleType = IStyled.EStyle.Regular;
        }

        vertex.selectActiveStyle(styleType);

        for(Edge edge : vertex.getIncomingEdges())
        {
            edge.selectActiveStyle(styleType);
            edge.getFrom().selectActiveStyle(styleType);
        }

        for(Edge edge : vertex.getOutgoingEdges())
        {
            edge.selectActiveStyle(styleType);
            edge.getTo().selectActiveStyle(styleType);
        }
    }

}
