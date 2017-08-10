package de.itemis.graphing.model.style;

public abstract class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }

    protected EShape shape = EShape.RoundedBox;

    public EShape getShape()
    {
        return shape;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }
}
