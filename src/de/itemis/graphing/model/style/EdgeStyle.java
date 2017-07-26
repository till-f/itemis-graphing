package de.itemis.graphing.model.style;

public class EdgeStyle extends Style
{
    public enum EShape { None, Arrow, Circle, Diamond }

    protected EShape shape = EShape.Arrow;

    public EShape getShape()
    {
        return shape;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
        updated();
    }

}
