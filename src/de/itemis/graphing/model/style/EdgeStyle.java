package de.itemis.graphing.model.style;

public class EdgeStyle extends Style
{
    public enum EShape { None, Arrow, Circle, Diamond }
    public enum EEdgeRouting { Direct, Cubic, Routed }

    protected EShape shape = EShape.Arrow;

    protected EEdgeRouting edgeRouting = EEdgeRouting.Direct;

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
        updated();
    }

    public void setEdgeRouting(EEdgeRouting edgeRouting)
    {
        this.edgeRouting = edgeRouting;
    }

}
