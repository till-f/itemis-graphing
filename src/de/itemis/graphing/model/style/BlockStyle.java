package de.itemis.graphing.model.style;

public abstract class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }
    public enum EShapeSize {Undefined, AsDefinedInStyle, InnerBlockSize, OuterBlockSize}

    protected EShape shape = EShape.RoundedBox;
    protected EShapeSize sizeMode = EShapeSize.InnerBlockSize;
    protected double width = 1.0;
    protected double height = 1.0;

    public EShape getShape()
    {
        return shape;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }

    public EShapeSize getSizeMode()
    {
        return sizeMode;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
        updated();
    }

    public void setWidth(Double width)
    {
        this.width = width;
        updated();
    }

    public void setHeight(Double height)
    {
        this.height = height;
        updated();
    }

    public void setSizeMode(EShapeSize sizeMode)
    {
        this.sizeMode = sizeMode;
        updated();
    }




}
