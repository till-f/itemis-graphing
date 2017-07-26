package de.itemis.graphing.model.style;

public abstract class BlockStyle extends Style
{
    public enum EShape { Box, RoundedBox, Circle }

    protected EShape shape = EShape.RoundedBox;
    protected Double width = null;
    protected Double height = null;
    protected Boolean takeElementSize = true;

    @Override
    public void mergeWith(Style newStyle)
    {
        super.mergeWith(newStyle);

        if (newStyle instanceof BlockStyle)
        {
            BlockStyle newBlockStyle = (BlockStyle) newStyle;
            shape = (newBlockStyle.shape == null) ? shape : newBlockStyle.shape;
            width = (newBlockStyle.width == null) ? width : newBlockStyle.width;
            height = (newBlockStyle.height == null) ? height : newBlockStyle.height;
            takeElementSize = (newBlockStyle.takeElementSize == null) ? takeElementSize : newBlockStyle.takeElementSize;
        }
    }

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

    public Boolean getTakeElementSize()
    {
        return takeElementSize;
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

    public void setTakeElementSize(Boolean takeElementSize)
    {
        this.takeElementSize = takeElementSize;
    }




}
