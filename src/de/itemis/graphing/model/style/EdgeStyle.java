package de.itemis.graphing.model.style;

public abstract class EdgeStyle extends Style
{
    public enum EShape {None, Arrow, Circle, Diamond }

    protected EShape shape = EShape.Arrow;

    @Override
    public void mergeWith(Style newStyle)
    {
        super.mergeWith(newStyle);

        if (newStyle instanceof EdgeStyle)
        {
            EdgeStyle newEdgeStyle = (EdgeStyle) newStyle;
            shape = (newEdgeStyle.shape == null) ? shape : newEdgeStyle.shape;
        }
    }

    public EShape getShape()
    {
        return shape;
    }

    public void setShape(EShape shape)
    {
        this.shape = shape;
    }

}
