package de.itemis.graphing.model.style;

public abstract class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }

    protected EShape shape = null;
    protected Double cornerRadiusPercent = null;

    public EShape getShape()
    {
        return shape;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }

    public Double getCornerRadiusPercent()
    {
        return cornerRadiusPercent;
    }

    public void setCornerRadiusPercent(Double cornerRadiusPercent)
    {
        this.cornerRadiusPercent = cornerRadiusPercent;
    }

    protected void setDefaults()
    {
        super.setDefaults();
        shape = EShape.RoundedBox;
        cornerRadiusPercent = 15.0;
    }

}
