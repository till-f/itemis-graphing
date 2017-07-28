package de.itemis.graphing.view.graphstream;

import de.itemis.graphing.model.BaseGraphElement;
import de.itemis.graphing.model.ISized;
import de.itemis.graphing.model.style.BlockStyle;
import de.itemis.graphing.model.style.EdgeStyle;
import de.itemis.graphing.model.style.Style;

public class StyleToGraphstreamCSS
{
    public String getAtciveStyleCSS(BaseGraphElement element)
    {
        StringBuilder sb = new StringBuilder();

        Style style = element.getActiveStyle();
        if (style instanceof BlockStyle)
        {
            BlockStyle blockStyle = (BlockStyle) style;

            sb.append("shape: " + getGraphstreamShape(blockStyle.getShape()) + ";");


            if (blockStyle.getSizeMode() == BlockStyle.ESizeMode.None)
            {
                sb.append("size-mode: fit;");
                sb.append("padding: 2;");
            }
            else
            {
                double width;
                double height;
                if (blockStyle.getSizeMode() == BlockStyle.ESizeMode.Explicit)
                {
                    width = blockStyle.getWidth();
                    height = blockStyle.getHeight();
                }
                else if (blockStyle.getSizeMode() == BlockStyle.ESizeMode.BaseSize)
                {
                    width = ((ISized) element).getInnerSize().getWidth();
                    height = ((ISized) element).getInnerSize().getHeight();
                }
                else if (blockStyle.getSizeMode() == BlockStyle.ESizeMode.FinalSize)
                {
                    width = ((ISized) element).getOuterSize().getWidth();
                    height = ((ISized) element).getOuterSize().getHeight();
                }
                else
                {
                    throw new IllegalArgumentException("invalid size mode: " + blockStyle.getSizeMode());
                }
                sb.append("size: " + width + "gu," + height + "gu" + ";");
            }

            sb.append("fill-mode: plain;");
            sb.append("fill-color: #" + style.getFillColor() + ";");
            sb.append("stroke-color: #" + style.getLineColor() + ";");
            sb.append("stroke-width: " + style.getLineThickness() + ";");
            sb.append("stroke-mode: " + getGraphstreamStrokeMode(style.getLineMode()) + ";");
        }
        else if (style instanceof EdgeStyle)
        {
            EdgeStyle edgeStyle = (EdgeStyle) style;

            sb.append("shape: cubic-curve;");
            sb.append("arrow-shape: " + getGraphstreamArrowShape(edgeStyle.getShape()) + ";");
            sb.append("fill-color: #" + style.getLineColor() + ";");

            if (style.getLineMode() == Style.ELineMode.None)
            {
                sb.append("stroke-mode: none;");
                sb.append("fill-mode: none;");
                sb.append("size: 0px;");
            }
            else if (style.getLineMode() == Style.ELineMode.Dotted)
            {
                sb.append("stroke-mode: dots;");
                sb.append("fill-mode: none;");
                sb.append("size: 0px;");
                sb.append("stroke-width: " + style.getLineThickness() + ";");
            }
            else if (style.getLineThickness() >= 1.0)
            {
                sb.append("stroke-mode: plain;");
                sb.append("stroke-width: " + (int)(style.getLineThickness()/2) + ";");
            }

            sb.append("stroke-color: #" + style.getLineColor() + ";");
            sb.append("fill-color: #" + style.getLineColor() + ";");
            sb.append("text-background-mode: rounded-box;");
            sb.append("text-background-color: white;");
            sb.append("text-padding: 2;");
        }

        sb.append("text-color: #" + style.getTextColor() + ";");
        sb.append("text-size: " + style.getFontSize() + ";");
        sb.append("z-index: " + style.getzIndex() + ";");

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
