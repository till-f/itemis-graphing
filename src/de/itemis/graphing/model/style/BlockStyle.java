package de.itemis.graphing.model.style;

public class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }
    public enum ESizeMode { None, Explicit, BaseSize, FinalSize }

    protected EShape shape = EShape.RoundedBox;
    protected ESizeMode sizeMode = ESizeMode.BaseSize;
    protected Double width = null;
    protected Double height = null;

    public EShape getShape()
    {
        return shape;
    }

    public Double getWidth()
    {
        return width;
    }

    public Double getHeight()
    {
        return height;
    }

    public ESizeMode getSizeMode()
    {
        return sizeMode;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }

    public void setWidth(Double width)
    {
        this.width = width;
    }

    public void setHeight(Double height)
    {
        this.height = height;
    }

    public void setSizeMode(ESizeMode sizeMode)
    {
        this.sizeMode = sizeMode;
    }




}
