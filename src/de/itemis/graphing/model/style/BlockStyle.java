package de.itemis.graphing.model.style;

public abstract class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }

    protected EShape shape = null;

    public EShape getShape()
    {
        return shape;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }

    protected void setDefaults()
    {
        super.setDefaults();
        shape = EShape.RoundedBox;
    }

}
