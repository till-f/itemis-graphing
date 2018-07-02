package de.itemis.graphing.model;

import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

public class Edge<T> extends GraphElement<T>
{
    private Vertex<T> _from;
    private Vertex<T> _to;

    private String _fromId;
    private String _toId;

    public Edge(Graph<T> g, String id, Vertex<T> from, Vertex<T> to)
    {
        super(g, id);
        _from = from;
        _fromId = from.getId();
        _to = to;
        _toId = to.getId();
        setIsSelectable(false);
    }

    public Edge(Graph<T> g, String id, String fromId, String toId)
    {
        super(g, id);
        _fromId = fromId;
        _toId = toId;
        setIsSelectable(false);
    }

    @Override
    protected void setInitialStyles()
    {
        _styleRegular = EdgeStyle.Default();
        _styleClicked = EdgeStyle.Empty();
        _styleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HLEDGE);
        _styleClicked.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleClicked.setIsInLevelForeground(true);
        _styleSelected = EdgeStyle.Empty();
        _styleSelected.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HLEDGE);
        _styleSelected.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleSelected.setIsInLevelForeground(true);

    }

    public Vertex<T> getFrom() {
        return _from;
    }

    public Vertex<T> getTo() {
        return _to;
    }

    public String getFromId() {
        return _fromId;
    }

    public String getToId() {
        return _toId;
    }

    public void setFromTo(Vertex<T> from, Vertex<T> to)
    {
        if (_from == null && _to == null)
        {
            _from = from;
            _to = to;
        }
        else
        {
            throw new IllegalArgumentException("source and target of an edge can only be set once");
        }
    }
}
