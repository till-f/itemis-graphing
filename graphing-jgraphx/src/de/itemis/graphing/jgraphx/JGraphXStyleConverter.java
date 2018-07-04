package de.itemis.graphing.jgraphx;

import com.mxgraph.util.mxConstants;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

public class JGraphXStyleConverter
{
    public String getStyleCSS(Style style)
    {
        StringBuilder sb = new StringBuilder();

        if (style instanceof BlockStyle)
        {
            sb.append(mxConstants.STYLE_VERTICAL_ALIGN + "=" + mxConstants.ALIGN_MIDDLE + ";");
            sb.append(mxConstants.STYLE_VERTICAL_LABEL_POSITION + "=" + mxConstants.ALIGN_MIDDLE + ";");
            BlockStyle blockStyle = (BlockStyle) style;
            if(blockStyle.getShape() == BlockStyle.EShape.RoundedBox)
            {
                sb.append(mxConstants.STYLE_ROUNDED + "=1;");
            }
            else
            {
                sb.append(mxConstants.STYLE_SHAPE + "=" + getJGraphXShape(blockStyle.getShape()) + ";");
                sb.append(mxConstants.STYLE_PERIMETER + "=" + getJGraphXShapePerimeter(blockStyle.getShape()) + ";");
            }

            sb.append(mxConstants.STYLE_FILLCOLOR + "=#" + style.getFillColor() + ";");
        }
        else if (style instanceof EdgeStyle)
        {
            EdgeStyle edgeStyle = (EdgeStyle) style;

            if (edgeStyle.getArrowOrientation() == EdgeStyle.EArrowOrientation.AsEdge)
            {
                sb.append(mxConstants.STYLE_STARTARROW + "=" + mxConstants.NONE + ";");
                sb.append(mxConstants.STYLE_ENDARROW + "=" + getJGraphXArrowShape(edgeStyle.getShape()) + ";");
            }
            else
            {
                sb.append(mxConstants.STYLE_STARTARROW + "=" + getJGraphXArrowShape(edgeStyle.getShape()) + ";");
                sb.append(mxConstants.STYLE_ENDARROW + "=" + mxConstants.NONE + ";");
            }
        }

        if (style.getLineMode() == Style.ELineMode.None)
        {
            sb.append(mxConstants.STYLE_STROKEWIDTH + "=0;");
        }
        else
        {
            sb.append(mxConstants.STYLE_STROKEWIDTH + "=" + style.getLineThickness() + ";");
            if (style.getLineMode() == Style.ELineMode.Dotted)
            {
                sb.append(mxConstants.STYLE_DASHED + "=1;");
            }
        }

        sb.append(mxConstants.STYLE_STROKECOLOR + "=#" + style.getLineColor() + ";");
        sb.append(mxConstants.STYLE_FONTCOLOR + "=#" + style.getTextColor() + ";");
        sb.append(mxConstants.STYLE_FONTSIZE + "=" + style.getFontSize() + ";");
        sb.append(mxConstants.STYLE_ALIGN + "=" + getJGraphXHorizontalAlignment(style.getLabelAlignment()) + ";");

        return sb.toString();
    }

    private String getJGraphXShape(BlockStyle.EShape shape)
    {
        switch (shape)
        {
            case Box:
            case RoundedBox:
                return mxConstants.SHAPE_RECTANGLE;
            case Circle:
                return mxConstants.SHAPE_ELLIPSE;
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getJGraphXShapePerimeter(BlockStyle.EShape shape)
    {
        switch (shape)
        {
            case Box:
            case RoundedBox:
                return mxConstants.PERIMETER_RECTANGLE;
            case Circle:
                return mxConstants.PERIMETER_ELLIPSE;
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getJGraphXArrowShape(EdgeStyle.EShape shape)
    {
        switch (shape)
        {
            case None:
                return mxConstants.ARROW_OPEN;
            case Arrow:
                return mxConstants.ARROW_CLASSIC;
            case Circle:
                return mxConstants.ARROW_OVAL;
            case Diamond:
                return mxConstants.ARROW_DIAMOND;
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getJGraphXHorizontalAlignment(Style.ELabelAlignment alignment)
    {
        switch (alignment)
        {
            case Center:
                return mxConstants.ALIGN_CENTER;
            case AtRight:
                return mxConstants.ALIGN_RIGHT;
            case AtLeft:
                return mxConstants.ALIGN_LEFT;
            default:
                return mxConstants.ALIGN_CENTER;
        }
    }

}
