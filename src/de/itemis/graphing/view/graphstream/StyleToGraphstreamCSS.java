package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.ISized;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

public class StyleToGraphstreamCSS
{
    public String getStyleString(BaseGraphElement element)
    {
        StringBuilder sb = new StringBuilder();

        Style style = element.retrieveStyle();
        if (style instanceof BlockStyle)
        {
            BlockStyle blockStyle = (BlockStyle) style;

            sb.append("shape: " + getGraphstreamShape(blockStyle.getShape()) + ";");

            Double width = blockStyle.getWidth();
            Double height = blockStyle.getHeight();
            if (blockStyle.getTakeElementSize() && element instanceof ISized)
            {
                width = ((ISized) element).getWidth();
                height = ((ISized) element).getHeight();
            }

            if (width != null && height != null)
            {
                sb.append("size: " + width + "gu," + height + "gu" + ";");
            }
            else
            {
                sb.append("size-mode: fit;");
                sb.append("padding: 2;");
            }

            if (style.getFillColor() != null)
            {
                sb.append("fill-mode: plain;");
                sb.append("fill-color: #" + style.getFillColor() + ";");
            }

            if (style.getLineColor() != null)
            {
                sb.append("stroke-color: #" + style.getLineColor() + ";");
            }

            if (style.getLineThickness() != null)
            {
                sb.append("stroke-width: " + style.getLineThickness() + ";");
            }

            if (style.getLineMode() != null)
            {
                sb.append("stroke-mode: " + getGraphstreamStrokeMode(style.getLineMode()) + ";");
            }
        }
        else if (style instanceof EdgeStyle)
        {
            EdgeStyle edgeStyle = (EdgeStyle) style;

            sb.append("shape: cubic-curve;");
            sb.append("arrow-shape: " + getGraphstreamArrowShape(edgeStyle.getShape()) + ";");

            if (style.getLineColor() != null)
            {
                sb.append("fill-color: #" + style.getLineColor() + ";");
            }

            if (style.getLineMode() != null && style.getLineMode() == Style.ELineMode.None)
            {
                sb.append("stroke-mode: none;");
                sb.append("fill-mode: none;");
                sb.append("size: 0px;");
            }
            else if (style.getLineMode() != null && style.getLineMode() == Style.ELineMode.Dotted)
            {
                sb.append("stroke-mode: dots;");
                sb.append("fill-mode: none;");
                sb.append("size: 0px;");
                sb.append("stroke-width: " + style.getLineThickness() + ";");
            }
            else if (style.getLineThickness() != null && style.getLineThickness() >= 2.0)
            {
                sb.append("stroke-mode: plain;");
                sb.append("stroke-width: " + (int)(style.getLineThickness()/2) + ";");
            }

            if (style.getLineColor() != null)
            {
                sb.append("stroke-color: #" + style.getLineColor() + ";");
                sb.append("fill-color: #" + style.getLineColor() + ";");
            }
        }

        if (style.getTextColor() != null)
        {
            sb.append("text-color: #" + style.getTextColor() + ";");
        }

        if (style.getFontSize() != null)
        {
            sb.append("text-size: " + style.getFontSize() + ";");
        }

        return sb.toString();
    }

    private String getGraphstreamShape(BlockStyle.EShape shape)
    {
        switch (shape)
        {
            case Box:
                return "box";
            case RoundedBox:
                return "rounded-box";
            case Circle:
                return "circle";
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getGraphstreamArrowShape(EdgeStyle.EShape shape)
    {
        switch (shape)
        {
            case None:
                return "none";
            case Arrow:
                return "arrow";
            case Circle:
                return "circle";
            case Diamond:
                return "diamond";
        }

        throw new IllegalArgumentException("invalid shape: " + shape);
    }

    private String getGraphstreamStrokeMode(Style.ELineMode mode)
    {
        switch (mode)
        {
            case None:
                return "none";
            case Solid:
                return "plain";
            case Dotted:
                return "dots";
        }

        throw new IllegalArgumentException("invalid mode: " + mode);
    }

}