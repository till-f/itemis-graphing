package de.itemis.graphing.model.style;

public class EdgeStyle extends Style
{
    public enum EShape { None, Arrow, Circle, Diamond }
    public enum EEdgeRouting { Direct, Cubic, Routed }

    protected EShape shape = null;
    protected EEdgeRouting edgeRouting = null;

    public static EdgeStyle Default()
    {
        EdgeStyle style = new EdgeStyle();
        style.setDefaults();
        return style;
    }

    public static EdgeStyle Empty()
    {
        return new EdgeStyle();
    }

    private EdgeStyle()
    {
        zIndex = 1;
    }

    public EShape getShape()
    {
        return shape;
    }

    public EEdgeRouting getEdgeRouting()
    {
        return edgeRouting;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }

    public void setEdgeRouting(EEdgeRouting edgeRouting)
    {
        this.edgeRouting = edgeRouting;
    }

    protected void setDefaults()
    {
        super.setDefaults();
        shape = EShape.Arrow;
        edgeRouting = EEdgeRouting.Direct;
    }

}
