package de.itemis.graphing.model.style;

public class VertexStyle extends BlockStyle
{
    public static VertexStyle Default()
    {
        VertexStyle style = new VertexStyle();
        style.setDefaults();
        return style;
    }

    public static VertexStyle Empty()
    {
        return new VertexStyle();
    }

    private VertexStyle()
    {
        zIndex = 3;
    }
}
