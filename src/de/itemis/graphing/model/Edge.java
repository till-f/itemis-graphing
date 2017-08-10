package de.itemis.graphing.model;

import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

import java.util.LinkedList;

public class Edge extends GraphElement
{
    private final LinkedList<Attachment> _attachments = new LinkedList<>();

    private Vertex _from;
    private Vertex _to;

    private String _fromId;
    private String _toId;

    public Edge(Graph g, String id, Vertex from, Vertex to)
    {
        super(g, id);
        _from = from;
        _to = to;
        setIsSelectable(false);
    }

    public Edge(Graph g, String id, String fromId, String toId)
    {
        super(g, id);
        _fromId = fromId;
        _toId = toId;
        setIsSelectable(false);
    }

    @Override
    protected void setInitialStyles()
    {
        _styleRegular = new EdgeStyle();
        _styleClicked = new EdgeStyle();
        _styleClicked.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HLEDGE);
        _styleClicked.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleClicked.setIsInLevelForeground(true);
        _styleSelected = new EdgeStyle();
        _styleSelected.setLineThickness(Style.DEFAULT_LINE_THICKNESS_HLEDGE);
        _styleSelected.setLineColor(Style.DEFAULT_LINE_COLOR_HL);
        _styleSelected.setIsInLevelForeground(true);

    }

    public Vertex getFrom() {
        return _from;
    }

    public Vertex getTo() {
        return _to;
    }

    public String getFromId() {
        return _fromId;
    }

    public String getToId() {
        return _toId;
    }

    public void setFromTo(Vertex from, Vertex to)
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
